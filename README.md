# tfdsfake
A location emulator based on Xposed framework. It's mainly used to signup for your daily work. (系统级的位置修改工具)

基于Rovo89的Xposed框架作品（link：https://github.com/rovo89/Xposed），运行百度地图SDK V4.3.1。
项目本是用在某公司的签到APP上的，在代码中还留着一些对com.ebai.pn包的一些HOOK。
由于主要是用来欺骗基于百度地图和系统自带地图的应用，该项目的核心代码是拦截BDLocation的get方法，传入假位置后返回的部分。
取用请随意，但请在使用时标明作者和来源。感激不尽！
