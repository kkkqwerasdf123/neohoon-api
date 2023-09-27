# 이것저것 공부

이것저것 공부

## Docker

### Install Docker

```shell
sudo apt-get update
sudo apt-get install apt-transport-https ca-certificates curl gnupg-agent software-properties-common
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -
sudo add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable"
sudo apt-get update
sudo apt-get install docker-ce docker-ce-cli containerd.io
```



### Server Firewall

cloud console & computing instance 에서 노드 간 방화벽 설정을 해 준다.

```shell
sudo iptables -I INPUT -p tcp -s 10.0.0.176 -j ACCEPT
sudo iptables -I INPUT -p udp -s 10.0.0.176 -j ACCEPT
```

### Docker Swarm

--advertise-addr 에는 privateIP 를 설정해 준다. (publicIP로 설정하니까 로드발란싱 이 잘 안된다)<br>
swarm init 해서 나온 명령어로 워커 노드에서 클러스터링을 한다.

- 매니저 노드 에서 swarm init
```shell
sudo docker swarm init --advertise-addr 10.0.0.54
```

- 매니저노드를 추가하기 위한 명령어 생성
```shell
docker swarm join-token --rotate manager
```

- 워커노드를 추가하기 위한 명령어 생성
```shell
docker swarm join-token --rotate worker
```

### gradle docker 플러그인

```groovy
plugins {
    ...
    id 'com.palantir.docker' version '0.35.0'
}

docker {
    name 'kkkqwerasdf123/dockertest'
    tag 'Dockerhub', "kkkqwerasdf123/dockertest:${project.version}"
    dockerfile file('Dockerfile')
    files tasks.bootJar.outputs.files
    buildArgs(['JAR_FILE': tasks.bootJar.outputs.files.singleFile.name])
    labels(['key': 'value'])
    pull true
    noCache true
}
```

### Create Docker Service

```shell
docker service create --name dockertest --replicas 4 --mount type=volume,source=dockertest_log,target=/var/log/dockertest -p 443:8080 -p 80:8080 --update-delay 10s --update-parallelism 2 --update-failure-action rollback kkkqwerasdf123/dockertest:0.0.6 --spring.profiles.active=dev
```

- 이미지 업데이트
```shell
docker service update --with-registry-auth --update-order start-first --image kkkqwerasdf123/dockertest:0.0.6 dockertest
```

## 애플리케이션

### SSL 설정

무료 인증서 발급 사이트(expire in 90 days)

> https://manage.sslforfree.com/

- 인증서를 발급받고 파일을 변환

```shell
openssl pkcs12 -export -out keystore.p12 -inkey private.key -in certificate.crt -certfile ca_bundle.crt
```

- application.properties 설정

```properties
server.ssl.enabled=true
server.ssl.enabled-protocols=TLSv1.1,TLSv1.2
server.ssl.key-store=classpath:ssl/keystore.p12
server.ssl.key-store-password=my_password
server.ssl.key-store-type=PKCS12
```

### http2 설정

```properties
server.http2.enabled=true
```




### view: SPA 사용하기 (gradle npm plugin)

frontend static 모드로 빌드<br>
서버 filter 나 controller 에서 SPA index.html 로 forward

```groovy
plugins {
    ...
    id "com.github.node-gradle.node" version "2.2.3"
}
node {
    version = '16.19.1'
    npmVersion = "8.19.1"
    download = true
}

task buildFront(type: NpmTask) {
    args = ['run', 'build']
}

processResources.dependsOn 'buildFront'
```