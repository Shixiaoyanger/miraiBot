# 一个基于[mirai](https://github.com/mamoe/mirai) 的多功能机器人

## 🌈目前支持的功能

- Splatoon2/喷射战士2  单排、组排图，打工图查询
- RSS   订阅你喜欢的RSS源，有更新会推送给你呦！
  - 一个[好用的RSS订阅源生成器](https://docs.rsshub.app/)
- 搜图  找到图片来源
- 涩图  来一份涩图🔞
- 一言  随机的一句话（鸡汤）
- 翻译  你的翻译小助手，支持多国语言互译

## ✨食用方法

1. 下载本仓库

    ```bash
    git clone https://github.com/Shixiaoyanger/miraiBot.git
    ```

2. 编译
> 请确保已安装JDK 8+环境，如何安装请自行搜索。

- windows:

    ```powershell
    gradlew shadowJar
    ```
    
- linux:

    ```bash
    ./gradlew shadowJar
    ```
    

    编译成功后，在`./build/libs`目录下会找到编译得到的jar包`MiraiBot-{version}-all.jar`

3. 运行

   - 将jar包复制到你想运行的位置;
   - 在命令终端输入`java -jar  MiraiBot-{version}-all.jar`即可运行。


   >  PS: 配置文件[config.yml](./config.yml)会在第一次运行项目后会在项目目录自动生成，可以根据实际需求修改`config.yml`中的配置，重启机器人生效。

4. 使用

   向机器人发送`帮助`、`help`就可以获取使用帮助啦🎉