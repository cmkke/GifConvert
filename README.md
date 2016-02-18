# GifConvert（视频转gif）

将视频文件转换成gif图片

# 演示

![v0.3](https://cloud.githubusercontent.com/assets/13044819/13077053/768771a6-d4f1-11e5-91cf-584dd645f6b0.gif)

# 实现原理

通过调用ffmpeg命令行将视频文件转换成gif图片，所以现在只能在Windows 64位上使用

# 代码

/src_media/ 和 /src_command/ 目录下是视频转换gif图片的实现

/src_looper/ 目录下是一个单线程线程池的实现，和Java API中的单线程线程池的不同是可以选择去除特定标记的线程
