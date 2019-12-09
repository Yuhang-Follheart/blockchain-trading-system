# blockchain-trading-system

A financial transaction system using fisco-bcos blockchain

---

## 环境要求

1. 建议使用**Ubuntu 18.04** 版本系统

2. **OpenJDK** 1.8以上版本

   不能用甲骨文官网的JDK,否则会连不上节点

3. **Gradle 建议使用6.0.1版本**

4. **fisco-bcos**

   下附安装教程

### 安装fisco-bcos

1. 安装 `openssl`, ` curl`

   `sudo apt install -y openssl curl`

2. 创建操作目录

   `cd ~ && mkdir -p fisco && cd fisco`

3. 下载`build_chain.sh`脚本

   `curl -LO https://github.com/FISCO-BCOS/FISCO-BCOS/releases/download/v2.1.0/build_chain.sh && chmod u+x build_chain.sh`

4. 搭建单群组4节点联盟链

   `bash build_chain.sh -l "127.0.0.1:4" -p 30300,20200,8545`

5. 启动节点

   `bash nodes/127.0.0.1/start_all.sh`

### **安装blockchain-trading-system**

1. clone 项目到本地

   `cd ~/ && git clone https://github.com/Yuhang-Follheart/blockchain-trading-system.git`

2. 复制区块链节点证书到项目资源目录

   `cd ~/fisco/nodes/127.0.0.1/sdk`

   `cp ca.crt sdk.crt sdk.key ~/blockchain-trading-system/Finance/src/main/resources`

3. 复制资源包到Gradle目录

   `cp -r ~/blockchain-trading-system/Dependencies/modules-2 ~/.gradle/caches/` 

4. 你可以使用你的IDE加载Finance文件夹作为project, 然后运行FGui.java, 也可以参照下面教程在终端执行

### 终端执行程序

1. build项目

   `cd ~/blockchain-trading-system/Finance`

   `gradle build`

2. 执行程序

   `gradle run`

### 生成账号私钥

节点登录需要使用账户私钥,这里给出生产账户私钥的方法

1. 获取脚本

   `curl -LO https://raw.githubusercontent.com/FISCO-BCOS/console/master/tools/get_account.sh && chmod u+x get_account.sh && bash get_account.sh -h`

2. 使用脚本生产私钥证书

   `bash get_account.sh`

## 程序演示视频

[演示视频](https://github.com/Yuhang-Follheart/blockchain-trading-system/blob/master/demo.mp4)
