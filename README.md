## LiuYi  App  DevLog

- [ ] 挑选每个页面的背景图 | 高度要统一
- [ ] 重设计主页 UI
- [ ] 重设计生成报告页 UI
- [ ] 卡片增加背景图
- [ ] 





### ~ - 5.15

#### Problems & Solving

- `fitsSystemWindows`  属性可用于实现沉浸式状态栏，但是使用不当会出现意向不到的结果

  比如，当 coordinatorLayout 和 AppBarLayout 都设置该属性时，就会出现下述结果：

  <img src="README_插图/image-20220515232944668.png" alt="image-20220515232944668" style="zoom:70%;" />

<img src="README_插图/image-20220515233036801.png" alt="image-20220515233036801" style="zoom:67%;" />

也就是图片无法填充满，而会有空当。

具体解释可参考该博客 `https://www.jianshu.com/p/9555f2386850`

大概感觉是这个原因

![image-20220515233153545](README_插图/image-20220515233153545.png)
