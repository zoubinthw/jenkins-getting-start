1.创建k8s集群 <br>
2.在集群中部署jenkins<br>
3.配置jenkins使用k8s集群作为job代理<br>
4.jenkins ci/cd

6.一些随笔:
(1)docker.io频繁提交会造成拒绝提交, 所以使用ecr, 这边要配置一下ecr的user并且给满ecr权限
(2)如果使用k8s agent, 在拉取k8s container作为jenkins file的部署环境时, 要带上securityContext, 否则部署不到k8s
 ```
 - name: kubectl
                image: bitnami/kubectl:latest
                command:
                - cat
                tty: true
                securityContext:
                  runAsUser: 1000
 ```
(3)如果要使用k8s的滚动更新策略, 就不要每次拉取latest版本的镜像, 带上每次部署的build no, 虽然也将latest提交上去
