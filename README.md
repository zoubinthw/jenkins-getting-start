1.创建k8s集群 <br>
2.在集群中部署jenkins<br>
3.配置jenkins使用k8s集群作为job代理<br>
4.jenkins ci/cd
5.终于实现了半自动ci/cd, 需要在代码提上去后手动build <br>
6.一些随笔:<br>
(1)使用流水线对docker.hub频繁提交, 会被docker hub拒绝提交, 所以使用ecr, 这边要配置一下ecr的user并且给满ecr权限<br>
(2)如果使用k8s agent, 在拉取k8s container作为jenkins file的部署环境时, 要带上securityContext, 否则部署不到k8s<br>
 ```
 - name: kubectl
                image: bitnami/kubectl:latest
                command:
                - cat
                tty: true
                securityContext:
                  runAsUser: 1000
 ```
(3)如果要使用k8s的滚动更新策略, 就不要每次拉取latest版本的镜像, 带上每次部署的build no, 虽然也将latest提交上去<br>
(4)如果要做k8s集群认证, 那么要为jenkins配置 secret file, 来自k8s api的config文件[.kube/config], 并且使用k8s集群内网url: https://kubernetes.default.svc
<br>要在config文件中写k8s api内网url, 否则无法授权<br>
(5)每次启动集群, 别忘了在从节点挂载nfs, 否则jenkins启动不了: mount -t nfs 172.31.10.22:/nfs/data /nfs/data <br>
(6)aws认证: 创建user -> 配置ecr权限 -> 得到access key & Secret access key -> 配置jenkins aws插件 -> 配置那两个key -> 配置一个id就以用id了
<br> docker认证的话, 配置用户名密码就好<br>
(7)其他的就是反正相关的插件带名字的都装上就好, 目前还没实现提交代码自动触发流水线, 虽然webhook也push到了jenkins, 但是jenkins没啥反应, 这个后面再看<br>
(8)jenkins file中environment和parameters的区别是:environment只能在step中使用, parameters可以在页面选择性的输入<br>
这边不用在environment中使用credentialId函数将jenkins中配置的credential id取出来(例如: XXX=credentialId('xxId'))<br>
(9)只有私有仓库要在jenkins流水线配置git仓库时, 需要配置credentials: Dashboard -> mvn-scm-demo(pipeline名字) -> Configuration -> Pipeline<br>
Definition: Pipeline script from SCM <br>
SCM: Git <br>
Repositories URL: https://github.com/zoubinthw/jenkins-getting-start.git 带.git的自己的仓库url <br>
Credentials: 留空 <br>
其他的jenkins按文档走 <br>
(10)Dashboard -> Manage Jenkins -> System -> GitHub 这里API URL配置: https://api.github.com, Credentials配置在github创建的access token
