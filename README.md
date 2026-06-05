🛠️ 技术栈
语言：Java (JDK 1.8)
GUI：Java Swing / AWT
音频：javax.sound.sampled（Java Sound API）
并发：java.util.Timer + TimerTask
构建/IDE：IntelliJ IDEA
▶️ 运行方式
环境要求
JDK 1.8 或更高版本
支持的音频格式：.wav（PCM 编码）
命令行编译运行
# 进入项目目录
cd 音乐播放器

# 编译
javac -encoding UTF-8 MusicPlay.java

# 运行
java MusicPlay
IDE 运行
使用 IntelliJ IDEA 打开项目，直接运行 MusicPlay 的 main 方法即可。

使用说明
点击「打开文件」按钮选择本地 .wav 音频，曲目会自动加入播放列表并开始播放
通过底部按钮进行播放/暂停、上一首、下一首操作
点击「播放模式」按钮在 顺序 / 单曲循环 / 随机 之间切换
点击「列表」按钮显示或隐藏播放列表，双击列表中曲目可直接切换播放
📂 项目结构
音乐播放器/
├── MusicPlay.java          # 全部源码（音频引擎 + 界面 + 入口）
├── background.png          # 播放器背景
├── play.png / stop.png     # 播放/暂停图标
├── prev.png / next.png     # 上一首/下一首图标
├── loop.png / repeat.png   # 播放模式图标
├── rand.png / list.png     # 随机播放 / 列表图标
├── 1.jpg / 2.jpg / 3.jpg   # 各曲目封面
└── README.md
💡 设计思考与已知限制
作为一个聚焦于"事件处理与音频处理"的实践项目，目前存在以下可优化空间，这些也是后续迭代方向：

歌词时间轴硬编码：当前歌词与时间断点写死在代码中，后续可改为解析标准 .lrc 歌词文件，实现任意歌曲的歌词同步。
音频格式有限：受 Java Sound 原生支持限制，目前仅支持 WAV；可引入解码层支持 MP3 等格式。
绝对布局：界面采用 setLayout(null) 绝对定位，可重构为响应式布局以适配窗口缩放。
架构分层：可进一步抽离为标准 MVC 三层，将播放列表数据模型从视图中独立出来。
能清晰地认识到当前实现的边界并规划演进路径，是我在这个项目中重要的收获之一。

📌 项目收获
通过这个项目，我深入实践了：

✅ Java Sound API 的底层音频流处理与精准播放控制
✅ 多线程编程：后台定时器与 UI 线程协作、生命周期管理
✅ 事件驱动与观察者模式在 GUI 中的应用
✅ 面向对象设计：关注点分离、接口封装
✅ 复杂 UI 的自定义渲染与状态管理
