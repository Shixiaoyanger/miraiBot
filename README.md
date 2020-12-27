一个基于[mirai](https://github.com/mamoe/mirai) 的多功能机器人

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
   - 将[config.yml](https://github.com/Shixiaoyanger/miraiBot/blob/master/config.yml)复制到与jar包同目录下；
   - 在[config.yml](https://github.com/Shixiaoyanger/miraiBot/blob/master/config.yml)中进行相关配置，如账号密码等；
   - 在命令终端输入`java -jar  MiraiBot-{version}-all.jar`即可运行。


   >  PS: `config.yml`也可以不复制，第一次运行项目后会在项目目录自动生成，修改生成的`config.yml`再次启动也可以哦。

4. 使用

   向机器人发送`帮助`、`help`就可以获取使用帮助啦🎉