# 不同GC和堆内存的总结

## GC

### 串行 GC

简单理解就是只能一个一个执行，执行期间所有线程都得暂停，俗称 STW（Stop The World）。就算你有多核 CPU 那你也得乖乖一个一个执行。

也是最初的的 GC 。现在基本不用。

串行 GC 对于年轻代使用 mark-copy（标记-复制）算法，对老年代使用 mark-sweep-compact（标记-清除-整理）算法

**使用场景：**

只适合几百兆堆内存的 JVM，而且是单核 CPU 比较有用。



### 并行 GC

并行GC 是在串行 GC 的基础上发展而来的。正是因为串行 GC 的缺陷导致使用 GC 的时候感觉很不爽。所以才有了并行 GC。

也是 JDK6，7，8 默认使用的 GC 策略。

并行 GC 在年轻代使用的 mark-copy（标记-复制）算法，在老年代使用的是 mark-sweep-compact（标记-清除-整理）算法

**使用场景：**

适用于多核CPU，主要目的就是提高吞吐量。



### CMS GC 

有些场景下还是觉得 GC 处理的时间（老年代）还是太长了。

所以为了进一步缩短每次 GC 暂停处理的时间，提出了分阶段处理。当然 CMS GC 也是优缺点的，因为没有对老年代区域进行压缩（有内存碎片）所以在某些情况下 GC 会造成不可预测的暂停时间，特别是内存比较大的情况。

- 阶段1：初始标记（Initial Mark）

  存在 STW

- 阶段2：并发标记（Concurrent Mark）

- 阶段3：并发预处理（Concurrent Preclean）

- 阶段4：最终标记（Final Remark）

  存在 STW

- 阶段5：并发处理（Concurrent Sweep）

- 阶段6：并发重置（Concurrent Reset）

**使用场景：**

多核 CPU，有明确的 ==低延迟== 要求。



### G1 GC

对于一些场景来说吞吐量优先级不是很高，GC 暂停处理的时间才是最不可忍受的。可以说是在牺牲一点吞吐量的前提下降低 GC 暂停处理的时间。那么 G1 GC 就来了。它可以说是跟之前几种 GC 都不一样。虽然说也有分阶段执行并且执行的步骤和 CMS GC 类似。

这也是 JDK9 及以后使用的 默认GC 策略了。

- 年轻代模式转移暂停（Evacuation Pause）
- 并发标记（Concurrent Marking） 和 CMS  GC 基本一样
  - 阶段1：初始标记（Initial Mark）
  - 阶段2：Root 区扫描（Root Region Mark）
  - 阶段3：并发标记（Concurrent Mark）
  - 阶段4：再次标记（Remark）
  - 阶段5：清理（Clean up）
- 转移暂停：混合模式（mixed）

当然设计还是有缺陷，主要我们特别注意，G1 GC 执行可能导致 Full GC ，然后这时候 G1 GC 就会退化成 串行 GC 。这你敢信？就直接从天堂到地狱的感觉。所以我们使用 G1 GC的时候还是需要注意 退化的问题！！！

**使用场景：**

对 STW 停顿时间要求可预期（但是千万要注意 G1 GC 退化的情况，那时就是不可预期了）



### 其他GC

之前讲过 ZGC 和 Shenandoah GC ；因为现在还是使用 JDK8 ，所以就咱先不讨论。



## 同一堆内存配置下不同GC的表现情况

### 串行GC

`java -XX:+UseSerialGC -Xms512m -Xmx512m -Xloggc:gc.demo1.log -XX:+PrintGCDetails -XX:+PrintGCDateStamps GCLogAnalysis`

```
正在执行...
2021-05-05T17:40:10.008+0800: [GC (Allocation Failure) 2021-05-05T17:40:10.008+0800: [DefNew: 139445K->17472K(157248K), 0.0215001 secs] 139445K->40713K(506816K), 0.0215773 secs] [Times: user=0.02 sys=0.00, real=0.02 secs] 
2021-05-05T17:40:10.051+0800: [GC (Allocation Failure) 2021-05-05T17:40:10.051+0800: [DefNew: 157248K->17471K(157248K), 0.0348485 secs] 180489K->82884K(506816K), 0.0349054 secs] [Times: user=0.02 sys=0.01, real=0.03 secs] 
2021-05-05T17:40:10.105+0800: [GC (Allocation Failure) 2021-05-05T17:40:10.105+0800: [DefNew: 156994K->17472K(157248K), 0.0288894 secs] 222408K->131609K(506816K), 0.0289392 secs] [Times: user=0.02 sys=0.02, real=0.03 secs] 
2021-05-05T17:40:10.161+0800: [GC (Allocation Failure) 2021-05-05T17:40:10.161+0800: [DefNew: 157248K->17468K(157248K), 0.0267257 secs] 271385K->176457K(506816K), 0.0267735 secs] [Times: user=0.02 sys=0.02, real=0.03 secs] 
2021-05-05T17:40:10.211+0800: [GC (Allocation Failure) 2021-05-05T17:40:10.211+0800: [DefNew: 157244K->17471K(157248K), 0.0259265 secs] 316233K->222249K(506816K), 0.0259760 secs] [Times: user=0.01 sys=0.02, real=0.03 secs] 
2021-05-05T17:40:10.258+0800: [GC (Allocation Failure) 2021-05-05T17:40:10.258+0800: [DefNew: 157247K->17469K(157248K), 0.0246716 secs] 362025K->265711K(506816K), 0.0247259 secs] [Times: user=0.03 sys=0.00, real=0.03 secs] 
2021-05-05T17:40:10.304+0800: [GC (Allocation Failure) 2021-05-05T17:40:10.304+0800: [DefNew: 157245K->17471K(157248K), 0.0259049 secs] 405487K->314987K(506816K), 0.0259496 secs] [Times: user=0.03 sys=0.00, real=0.03 secs] 
2021-05-05T17:40:10.349+0800: [GC (Allocation Failure) 2021-05-05T17:40:10.349+0800: [DefNew: 157247K->157247K(157248K), 0.0000220 secs]2021-05-05T17:40:10.349+0800: [Tenured: 297515K->264192K(349568K), 0.0386306 secs] 454763K->264192K(506816K), [Metaspace: 3508K->3508K(1056768K)], 0.0387280 secs] [Times: user=0.03 sys=0.00, real=0.04 secs] 
2021-05-05T17:40:10.406+0800: [GC (Allocation Failure) 2021-05-05T17:40:10.406+0800: [DefNew: 139776K->17472K(157248K), 0.0100588 secs] 403968K->317472K(506816K), 0.0101059 secs] [Times: user=0.00 sys=0.00, real=0.01 secs] 
2021-05-05T17:40:10.436+0800: [GC (Allocation Failure) 2021-05-05T17:40:10.436+0800: [DefNew: 157248K->157248K(157248K), 0.0000212 secs]2021-05-05T17:40:10.436+0800: [Tenured: 300000K->299563K(349568K), 0.0418805 secs] 457248K->299563K(506816K), [Metaspace: 3508K->3508K(1056768K)], 0.0419704 secs] [Times: user=0.05 sys=0.00, real=0.04 secs] 
2021-05-05T17:40:10.497+0800: [GC (Allocation Failure) 2021-05-05T17:40:10.497+0800: [DefNew: 139776K->17471K(157248K), 0.0141044 secs] 439339K->341992K(506816K), 0.0141580 secs] [Times: user=0.02 sys=0.00, real=0.01 secs] 
2021-05-05T17:40:10.529+0800: [GC (Allocation Failure) 2021-05-05T17:40:10.529+0800: [DefNew: 157003K->157003K(157248K), 0.0000213 secs]2021-05-05T17:40:10.529+0800: [Tenured: 324520K->308845K(349568K), 0.0475679 secs] 481524K->308845K(506816K), [Metaspace: 3508K->3508K(1056768K)], 0.0476601 secs] [Times: user=0.05 sys=0.00, real=0.05 secs] 
2021-05-05T17:40:10.597+0800: [GC (Allocation Failure) 2021-05-05T17:40:10.597+0800: [DefNew: 139722K->139722K(157248K), 0.0000219 secs]2021-05-05T17:40:10.597+0800: [Tenured: 308845K->295926K(349568K), 0.0465417 secs] 448567K->295926K(506816K), [Metaspace: 3508K->3508K(1056768K)], 0.0466461 secs] [Times: user=0.05 sys=0.00, real=0.05 secs] 
2021-05-05T17:40:10.664+0800: [GC (Allocation Failure) 2021-05-05T17:40:10.664+0800: [DefNew: 139742K->17471K(157248K), 0.0112875 secs] 435669K->347476K(506816K), 0.0113312 secs] [Times: user=0.02 sys=0.00, real=0.01 secs] 
2021-05-05T17:40:10.703+0800: [GC (Allocation Failure) 2021-05-05T17:40:10.703+0800: [DefNew: 157247K->157247K(157248K), 0.0000212 secs]2021-05-05T17:40:10.703+0800: [Tenured: 330004K->347124K(349568K), 0.0508097 secs] 487252K->347124K(506816K), [Metaspace: 3508K->3508K(1056768K)], 0.0509083 secs] [Times: user=0.03 sys=0.02, real=0.05 secs] 
2021-05-05T17:40:10.776+0800: [GC (Allocation Failure) 2021-05-05T17:40:10.776+0800: [DefNew: 139776K->139776K(157248K), 0.0000363 secs]2021-05-05T17:40:10.776+0800: [Tenured: 347124K->348883K(349568K), 0.0574132 secs] 486900K->348883K(506816K), [Metaspace: 3508K->3508K(1056768K)], 0.0575322 secs] [Times: user=0.06 sys=0.00, real=0.06 secs] 
2021-05-05T17:40:10.860+0800: [GC (Allocation Failure) 2021-05-05T17:40:10.860+0800: [DefNew: 139776K->139776K(157248K), 0.0000213 secs]2021-05-05T17:40:10.860+0800: [Tenured: 348883K->349341K(349568K), 0.0544035 secs] 488659K->355513K(506816K), [Metaspace: 3829K->3829K(1056768K)], 0.0544871 secs] [Times: user=0.05 sys=0.00, real=0.06 secs] 
执行结束!共生成对象次数:9182
Heap
 def new generation   total 157248K, used 54283K [0x00000000e0000000, 0x00000000eaaa0000, 0x00000000eaaa0000)
  eden space 139776K,  38% used [0x00000000e0000000, 0x00000000e3502c20, 0x00000000e8880000)
  from space 17472K,   0% used [0x00000000e8880000, 0x00000000e8880000, 0x00000000e9990000)
  to   space 17472K,   0% used [0x00000000e9990000, 0x00000000e9990000, 0x00000000eaaa0000)
 tenured generation   total 349568K, used 349341K [0x00000000eaaa0000, 0x0000000100000000, 0x0000000100000000)
   the space 349568K,  99% used [0x00000000eaaa0000, 0x00000000fffc7640, 0x00000000fffc7800, 0x0000000100000000)
 Metaspace       used 4004K, capacity 4572K, committed 4864K, reserved 1056768K
  class space    used 443K, capacity 460K, committed 512K, reserved 1048576K
```

- DefNew 就是说的是 Yong GC 

可以看到有 Yong GC 和 Full GC以及各个区域的使用情况

### 并行GC

`java -XX:+UseParallelGC -Xms512m -Xmx512m -Xloggc:gc.demo2.log -XX:+PrintGCDetails -XX:+PrintGCDateStamps GCLogAnalysis`

```
正在执行...
2021-05-05T17:46:43.249+0800: [GC (Allocation Failure) [PSYoungGen: 131584K->21498K(153088K)] 131584K->41705K(502784K), 0.0089844 secs] [Times: user=0.00 sys=0.02, real=0.01 secs] 
2021-05-05T17:46:43.280+0800: [GC (Allocation Failure) [PSYoungGen: 153082K->21494K(153088K)] 173289K->84476K(502784K), 0.0114201 secs] [Times: user=0.05 sys=0.06, real=0.01 secs] 
2021-05-05T17:46:43.318+0800: [GC (Allocation Failure) [PSYoungGen: 152778K->21489K(153088K)] 215760K->129476K(502784K), 0.0101866 secs] [Times: user=0.00 sys=0.00, real=0.01 secs] 
2021-05-05T17:46:43.348+0800: [GC (Allocation Failure) [PSYoungGen: 152870K->21502K(153088K)] 260858K->170785K(502784K), 0.0096646 secs] [Times: user=0.00 sys=0.00, real=0.01 secs] 
2021-05-05T17:46:43.379+0800: [GC (Allocation Failure) [PSYoungGen: 153086K->21498K(153088K)] 302369K->210351K(502784K), 0.0106236 secs] [Times: user=0.00 sys=0.00, real=0.01 secs] 
2021-05-05T17:46:43.408+0800: [GC (Allocation Failure) [PSYoungGen: 153082K->21498K(80384K)] 341935K->256875K(430080K), 0.0136030 secs] [Times: user=0.00 sys=0.01, real=0.01 secs] 
2021-05-05T17:46:43.431+0800: [GC (Allocation Failure) [PSYoungGen: 80378K->38075K(116736K)] 315755K->276972K(466432K), 0.0062303 secs] [Times: user=0.00 sys=0.00, real=0.01 secs] 
2021-05-05T17:46:43.447+0800: [GC (Allocation Failure) [PSYoungGen: 96884K->50408K(116736K)] 335781K->295547K(466432K), 0.0083370 secs] [Times: user=0.06 sys=0.03, real=0.01 secs] 
2021-05-05T17:46:43.464+0800: [GC (Allocation Failure) [PSYoungGen: 109191K->57851K(116736K)] 354331K->316527K(466432K), 0.0100378 secs] [Times: user=0.11 sys=0.00, real=0.01 secs] 
2021-05-05T17:46:43.483+0800: [GC (Allocation Failure) [PSYoungGen: 116724K->38952K(116736K)] 375400K->332110K(466432K), 0.0103213 secs] [Times: user=0.08 sys=0.03, real=0.01 secs] 
2021-05-05T17:46:43.493+0800: [Full GC (Ergonomics) [PSYoungGen: 38952K->0K(116736K)] [ParOldGen: 293158K->236714K(349696K)] 332110K->236714K(466432K), [Metaspace: 3504K->3504K(1056768K)], 0.0377004 secs] [Times: user=0.25 sys=0.00, real=0.04 secs] 
2021-05-05T17:46:43.540+0800: [GC (Allocation Failure) [PSYoungGen: 58880K->22132K(116736K)] 295594K->258846K(466432K), 0.0035852 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-05-05T17:46:43.552+0800: [GC (Allocation Failure) [PSYoungGen: 80773K->18243K(116736K)] 317488K->276200K(466432K), 0.0057245 secs] [Times: user=0.00 sys=0.00, real=0.01 secs] 
2021-05-05T17:46:43.570+0800: [GC (Allocation Failure) [PSYoungGen: 77123K->25924K(116736K)] 335080K->300544K(466432K), 0.0057067 secs] [Times: user=0.00 sys=0.00, real=0.01 secs] 
2021-05-05T17:46:43.587+0800: [GC (Allocation Failure) [PSYoungGen: 84804K->18380K(116736K)] 359424K->317259K(466432K), 0.0066245 secs] [Times: user=0.00 sys=0.00, real=0.01 secs] 
2021-05-05T17:46:43.594+0800: [Full GC (Ergonomics) [PSYoungGen: 18380K->0K(116736K)] [ParOldGen: 298879K->265340K(349696K)] 317259K->265340K(466432K), [Metaspace: 3505K->3505K(1056768K)], 0.0389281 secs] [Times: user=0.22 sys=0.00, real=0.04 secs] 
2021-05-05T17:46:43.641+0800: [GC (Allocation Failure) [PSYoungGen: 58880K->19394K(116736K)] 324220K->284734K(466432K), 0.0034828 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-05-05T17:46:43.655+0800: [GC (Allocation Failure) [PSYoungGen: 78274K->19999K(116736K)] 343614K->303774K(466432K), 0.0062871 secs] [Times: user=0.09 sys=0.00, real=0.01 secs] 
2021-05-05T17:46:43.671+0800: [GC (Allocation Failure) [PSYoungGen: 78879K->19765K(116736K)] 362654K->323054K(466432K), 0.0054558 secs] [Times: user=0.09 sys=0.02, real=0.01 secs] 
2021-05-05T17:46:43.685+0800: [GC (Allocation Failure) [PSYoungGen: 78586K->18251K(116736K)] 381875K->340472K(466432K), 0.0056480 secs] [Times: user=0.03 sys=0.09, real=0.01 secs] 
2021-05-05T17:46:43.691+0800: [Full GC (Ergonomics) [PSYoungGen: 18251K->0K(116736K)] [ParOldGen: 322221K->286262K(349696K)] 340472K->286262K(466432K), [Metaspace: 3505K->3505K(1056768K)], 0.0401380 secs] [Times: user=0.19 sys=0.00, real=0.04 secs] 
2021-05-05T17:46:43.741+0800: [GC (Allocation Failure) [PSYoungGen: 58880K->19131K(116736K)] 345142K->305393K(466432K), 0.0029583 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-05-05T17:46:43.753+0800: [GC (Allocation Failure) [PSYoungGen: 78011K->20352K(116736K)] 364273K->325464K(466432K), 0.0052359 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-05-05T17:46:43.767+0800: [GC (Allocation Failure) [PSYoungGen: 79112K->19894K(116736K)] 384224K->344133K(466432K), 0.0055762 secs] [Times: user=0.00 sys=0.00, real=0.01 secs] 
2021-05-05T17:46:43.772+0800: [Full GC (Ergonomics) [PSYoungGen: 19894K->0K(116736K)] [ParOldGen: 324238K->297267K(349696K)] 344133K->297267K(466432K), [Metaspace: 3505K->3505K(1056768K)], 0.0439839 secs] [Times: user=0.27 sys=0.00, real=0.04 secs] 
2021-05-05T17:46:43.827+0800: [GC (Allocation Failure) [PSYoungGen: 58880K->20002K(116736K)] 356147K->317269K(466432K), 0.0034789 secs] [Times: user=0.08 sys=0.00, real=0.00 secs] 
2021-05-05T17:46:43.844+0800: [GC (Allocation Failure) [PSYoungGen: 78882K->16963K(116736K)] 376149K->332419K(466432K), 0.0052091 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-05-05T17:46:43.863+0800: [GC (Allocation Failure) [PSYoungGen: 75843K->23081K(116736K)] 391299K->354608K(466432K), 0.0061889 secs] [Times: user=0.00 sys=0.00, real=0.01 secs] 
2021-05-05T17:46:43.869+0800: [Full GC (Ergonomics) [PSYoungGen: 23081K->0K(116736K)] [ParOldGen: 331527K->304310K(349696K)] 354608K->304310K(466432K), [Metaspace: 3505K->3505K(1056768K)], 0.0440619 secs] [Times: user=0.23 sys=0.02, real=0.04 secs] 
2021-05-05T17:46:43.923+0800: [GC (Allocation Failure) [PSYoungGen: 58873K->22844K(116736K)] 363184K->327155K(466432K), 0.0039157 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-05-05T17:46:43.938+0800: [GC (Allocation Failure) [PSYoungGen: 81724K->23675K(116736K)] 386035K->349675K(466432K), 0.0064089 secs] [Times: user=0.00 sys=0.00, real=0.01 secs] 
2021-05-05T17:46:43.944+0800: [Full GC (Ergonomics) [PSYoungGen: 23675K->0K(116736K)] [ParOldGen: 325999K->316961K(349696K)] 349675K->316961K(466432K), [Metaspace: 3505K->3505K(1056768K)], 0.0443807 secs] [Times: user=0.22 sys=0.00, real=0.05 secs] 
2021-05-05T17:46:43.998+0800: [GC (Allocation Failure) [PSYoungGen: 58589K->25436K(116736K)] 375550K->342397K(466432K), 0.0042553 secs] [Times: user=0.08 sys=0.00, real=0.00 secs] 
2021-05-05T17:46:44.012+0800: [GC (Allocation Failure) [PSYoungGen: 84316K->46007K(116736K)] 401277K->362968K(466432K), 0.0069966 secs] [Times: user=0.06 sys=0.00, real=0.01 secs] 
2021-05-05T17:46:44.031+0800: [GC (Allocation Failure) --[PSYoungGen: 104833K->104833K(116736K)] 421794K->454500K(466432K), 0.0095759 secs] [Times: user=0.08 sys=0.00, real=0.01 secs] 
2021-05-05T17:46:44.041+0800: [Full GC (Ergonomics) [PSYoungGen: 104833K->0K(116736K)] [ParOldGen: 349666K->326766K(349696K)] 454500K->326766K(466432K), [Metaspace: 3505K->3505K(1056768K)], 0.0545646 secs] [Times: user=0.20 sys=0.02, real=0.05 secs] 
2021-05-05T17:46:44.109+0800: [Full GC (Ergonomics) [PSYoungGen: 58856K->0K(116736K)] [ParOldGen: 326766K->331183K(349696K)] 385622K->331183K(466432K), [Metaspace: 3765K->3765K(1056768K)], 0.0538341 secs] [Times: user=0.23 sys=0.01, real=0.05 secs] 
执行结束!共生成对象次数:8366
Heap
 PSYoungGen      total 116736K, used 16120K [0x00000000f5580000, 0x0000000100000000, 0x0000000100000000)
  eden space 58880K, 27% used [0x00000000f5580000,0x00000000f653e248,0x00000000f8f00000)
  from space 57856K, 0% used [0x00000000fc780000,0x00000000fc780000,0x0000000100000000)
  to   space 57856K, 0% used [0x00000000f8f00000,0x00000000f8f00000,0x00000000fc780000)
 ParOldGen       total 349696K, used 331183K [0x00000000e0000000, 0x00000000f5580000, 0x00000000f5580000)
  object space 349696K, 94% used [0x00000000e0000000,0x00000000f436bed0,0x00000000f5580000)
 Metaspace       used 3811K, capacity 4540K, committed 4864K, reserved 1056768K
  class space    used 423K, capacity 428K, committed 512K, reserved 1048576K	
```

- PSYoungGen 说的也是 Yong GC

可以看到 也是出现了几次 Yong GC 和 Full GC；各个区域的使用情况和 串行 GC 没什么明显变化。

### CMS GC

`java -XX:+UseConcMarkSweepGC -Xms512m -Xmx512m -Xloggc:gc.demo3.log -XX:+PrintGCDetails -XX:+PrintGCDateStamps GCLogAnalysis`

```
正在执行...
2021-05-05T17:47:26.944+0800: [GC (Allocation Failure) 2021-05-05T17:47:26.944+0800: [ParNew: 139776K->17471K(157248K), 0.0087123 secs] 139776K->44064K(506816K), 0.0088018 secs] [Times: user=0.09 sys=0.00, real=0.01 secs] 
2021-05-05T17:47:26.974+0800: [GC (Allocation Failure) 2021-05-05T17:47:26.974+0800: [ParNew: 157247K->17470K(157248K), 0.0128831 secs] 183840K->86444K(506816K), 0.0129490 secs] [Times: user=0.05 sys=0.08, real=0.01 secs] 
2021-05-05T17:47:27.007+0800: [GC (Allocation Failure) 2021-05-05T17:47:27.007+0800: [ParNew: 157246K->17471K(157248K), 0.0299087 secs] 226220K->132796K(506816K), 0.0299552 secs] [Times: user=0.17 sys=0.03, real=0.03 secs] 
2021-05-05T17:47:27.057+0800: [GC (Allocation Failure) 2021-05-05T17:47:27.057+0800: [ParNew: 157247K->17470K(157248K), 0.0303449 secs] 272572K->181714K(506816K), 0.0303989 secs] [Times: user=0.17 sys=0.03, real=0.03 secs] 
2021-05-05T17:47:27.113+0800: [GC (Allocation Failure) 2021-05-05T17:47:27.113+0800: [ParNew: 157213K->17470K(157248K), 0.0247687 secs] 321456K->222534K(506816K), 0.0248358 secs] [Times: user=0.08 sys=0.01, real=0.02 secs] 
2021-05-05T17:47:27.138+0800: [GC (CMS Initial Mark) [1 CMS-initial-mark: 205063K(349568K)] 225399K(506816K), 0.0003074 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-05-05T17:47:27.138+0800: [CMS-concurrent-mark-start]
2021-05-05T17:47:27.140+0800: [CMS-concurrent-mark: 0.002/0.002 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-05-05T17:47:27.140+0800: [CMS-concurrent-preclean-start]
2021-05-05T17:47:27.140+0800: [CMS-concurrent-preclean: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-05-05T17:47:27.141+0800: [CMS-concurrent-abortable-preclean-start]
2021-05-05T17:47:27.158+0800: [GC (Allocation Failure) 2021-05-05T17:47:27.158+0800: [ParNew: 157246K->17471K(157248K), 0.0270551 secs] 362310K->265427K(506816K), 0.0271147 secs] [Times: user=0.08 sys=0.02, real=0.03 secs] 
2021-05-05T17:47:27.208+0800: [GC (Allocation Failure) 2021-05-05T17:47:27.208+0800: [ParNew: 157061K->17468K(157248K), 0.0275041 secs] 405017K->309101K(506816K), 0.0275552 secs] [Times: user=0.20 sys=0.00, real=0.03 secs] 
2021-05-05T17:47:27.256+0800: [GC (Allocation Failure) 2021-05-05T17:47:27.256+0800: [ParNew: 156953K->17471K(157248K), 0.0280982 secs] 448586K->353613K(506816K), 0.0281460 secs] [Times: user=0.16 sys=0.06, real=0.03 secs] 
2021-05-05T17:47:27.284+0800: [CMS-concurrent-abortable-preclean: 0.003/0.144 secs] [Times: user=0.50 sys=0.08, real=0.14 secs] 
2021-05-05T17:47:27.285+0800: [GC (CMS Final Remark) [YG occupancy: 20556 K (157248 K)]2021-05-05T17:47:27.285+0800: [Rescan (parallel) , 0.0004669 secs]2021-05-05T17:47:27.285+0800: [weak refs processing, 0.0000180 secs]2021-05-05T17:47:27.285+0800: [class unloading, 0.0002542 secs]2021-05-05T17:47:27.285+0800: [scrub symbol table, 0.0004454 secs]2021-05-05T17:47:27.286+0800: [scrub string table, 0.0001120 secs][1 CMS-remark: 336141K(349568K)] 356698K(506816K), 0.0013871 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-05-05T17:47:27.286+0800: [CMS-concurrent-sweep-start]
2021-05-05T17:47:27.287+0800: [CMS-concurrent-sweep: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-05-05T17:47:27.287+0800: [CMS-concurrent-reset-start]
2021-05-05T17:47:27.288+0800: [CMS-concurrent-reset: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-05-05T17:47:27.308+0800: [GC (Allocation Failure) 2021-05-05T17:47:27.308+0800: [ParNew: 157247K->17470K(157248K), 0.0136557 secs] 459423K->360914K(506816K), 0.0137429 secs] [Times: user=0.11 sys=0.00, real=0.01 secs] 
2021-05-05T17:47:27.322+0800: [GC (CMS Initial Mark) [1 CMS-initial-mark: 343443K(349568K)] 361202K(506816K), 0.0002012 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-05-05T17:47:27.322+0800: [CMS-concurrent-mark-start]
2021-05-05T17:47:27.323+0800: [CMS-concurrent-mark: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-05-05T17:47:27.323+0800: [CMS-concurrent-preclean-start]
2021-05-05T17:47:27.324+0800: [CMS-concurrent-preclean: 0.000/0.000 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-05-05T17:47:27.324+0800: [CMS-concurrent-abortable-preclean-start]
2021-05-05T17:47:27.324+0800: [CMS-concurrent-abortable-preclean: 0.000/0.000 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-05-05T17:47:27.324+0800: [GC (CMS Final Remark) [YG occupancy: 28437 K (157248 K)]2021-05-05T17:47:27.324+0800: [Rescan (parallel) , 0.0004083 secs]2021-05-05T17:47:27.325+0800: [weak refs processing, 0.0000145 secs]2021-05-05T17:47:27.325+0800: [class unloading, 0.0002701 secs]2021-05-05T17:47:27.325+0800: [scrub symbol table, 0.0005188 secs]2021-05-05T17:47:27.325+0800: [scrub string table, 0.0001535 secs][1 CMS-remark: 343443K(349568K)] 371880K(506816K), 0.0014609 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-05-05T17:47:27.326+0800: [CMS-concurrent-sweep-start]
2021-05-05T17:47:27.327+0800: [CMS-concurrent-sweep: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-05-05T17:47:27.327+0800: [CMS-concurrent-reset-start]
2021-05-05T17:47:27.327+0800: [CMS-concurrent-reset: 0.000/0.000 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-05-05T17:47:27.351+0800: [GC (Allocation Failure) 2021-05-05T17:47:27.351+0800: [ParNew: 157238K->17471K(157248K), 0.0104307 secs] 405187K->306151K(506816K), 0.0104806 secs] [Times: user=0.11 sys=0.00, real=0.01 secs] 
2021-05-05T17:47:27.362+0800: [GC (CMS Initial Mark) [1 CMS-initial-mark: 288680K(349568K)] 309025K(506816K), 0.0002035 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-05-05T17:47:27.362+0800: [CMS-concurrent-mark-start]
2021-05-05T17:47:27.364+0800: [CMS-concurrent-mark: 0.002/0.002 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-05-05T17:47:27.364+0800: [CMS-concurrent-preclean-start]
2021-05-05T17:47:27.364+0800: [CMS-concurrent-preclean: 0.000/0.000 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-05-05T17:47:27.364+0800: [CMS-concurrent-abortable-preclean-start]
2021-05-05T17:47:27.381+0800: [GC (Allocation Failure) 2021-05-05T17:47:27.381+0800: [ParNew: 157247K->17470K(157248K), 0.0113400 secs] 445927K->351658K(506816K), 0.0113960 secs] [Times: user=0.09 sys=0.02, real=0.01 secs] 
2021-05-05T17:47:27.393+0800: [CMS-concurrent-abortable-preclean: 0.000/0.029 secs] [Times: user=0.11 sys=0.02, real=0.03 secs] 
2021-05-05T17:47:27.393+0800: [GC (CMS Final Remark) [YG occupancy: 20463 K (157248 K)]2021-05-05T17:47:27.393+0800: [Rescan (parallel) , 0.0004114 secs]2021-05-05T17:47:27.393+0800: [weak refs processing, 0.0000232 secs]2021-05-05T17:47:27.393+0800: [class unloading, 0.0002346 secs]2021-05-05T17:47:27.393+0800: [scrub symbol table, 0.0005331 secs]2021-05-05T17:47:27.394+0800: [scrub string table, 0.0001225 secs][1 CMS-remark: 334187K(349568K)] 354651K(506816K), 0.0014128 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-05-05T17:47:27.394+0800: [CMS-concurrent-sweep-start]
2021-05-05T17:47:27.395+0800: [CMS-concurrent-sweep: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-05-05T17:47:27.395+0800: [CMS-concurrent-reset-start]
2021-05-05T17:47:27.395+0800: [CMS-concurrent-reset: 0.000/0.000 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-05-05T17:47:27.415+0800: [GC (Allocation Failure) 2021-05-05T17:47:27.415+0800: [ParNew: 157246K->157246K(157248K), 0.0000255 secs]2021-05-05T17:47:27.415+0800: [CMS: 307941K->307871K(349568K), 0.0484752 secs] 465188K->307871K(506816K), [Metaspace: 3508K->3508K(1056768K)], 0.0485791 secs] [Times: user=0.05 sys=0.00, real=0.05 secs] 
2021-05-05T17:47:27.464+0800: [GC (CMS Initial Mark) [1 CMS-initial-mark: 307871K(349568K)] 308016K(506816K), 0.0002533 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-05-05T17:47:27.464+0800: [CMS-concurrent-mark-start]
2021-05-05T17:47:27.466+0800: [CMS-concurrent-mark: 0.002/0.002 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-05-05T17:47:27.466+0800: [CMS-concurrent-preclean-start]
2021-05-05T17:47:27.467+0800: [CMS-concurrent-preclean: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-05-05T17:47:27.467+0800: [CMS-concurrent-abortable-preclean-start]
2021-05-05T17:47:27.497+0800: [GC (Allocation Failure) 2021-05-05T17:47:27.497+0800: [ParNew: 139776K->17471K(157248K), 0.0087699 secs] 447647K->357138K(506816K), 0.0088259 secs] [Times: user=0.09 sys=0.00, real=0.01 secs] 
2021-05-05T17:47:27.506+0800: [CMS-concurrent-abortable-preclean: 0.000/0.039 secs] [Times: user=0.13 sys=0.00, real=0.04 secs] 
2021-05-05T17:47:27.506+0800: [GC (CMS Final Remark) [YG occupancy: 20354 K (157248 K)]2021-05-05T17:47:27.506+0800: [Rescan (parallel) , 0.0003818 secs]2021-05-05T17:47:27.506+0800: [weak refs processing, 0.0000216 secs]2021-05-05T17:47:27.506+0800: [class unloading, 0.0003263 secs]2021-05-05T17:47:27.507+0800: [scrub symbol table, 0.0007476 secs]2021-05-05T17:47:27.507+0800: [scrub string table, 0.0001934 secs][1 CMS-remark: 339666K(349568K)] 360021K(506816K), 0.0018079 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-05-05T17:47:27.508+0800: [CMS-concurrent-sweep-start]
2021-05-05T17:47:27.509+0800: [CMS-concurrent-sweep: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-05-05T17:47:27.509+0800: [CMS-concurrent-reset-start]
2021-05-05T17:47:27.509+0800: [CMS-concurrent-reset: 0.000/0.000 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-05-05T17:47:27.532+0800: [GC (Allocation Failure) 2021-05-05T17:47:27.532+0800: [ParNew: 157247K->157247K(157248K), 0.0000525 secs]2021-05-05T17:47:27.532+0800: [CMS: 338406K->318323K(349568K), 0.0654946 secs] 495653K->318323K(506816K), [Metaspace: 3508K->3508K(1056768K)], 0.0656560 secs] [Times: user=0.06 sys=0.00, real=0.06 secs] 
2021-05-05T17:47:27.598+0800: [GC (CMS Initial Mark) [1 CMS-initial-mark: 318323K(349568K)] 318898K(506816K), 0.0002928 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-05-05T17:47:27.598+0800: [CMS-concurrent-mark-start]
2021-05-05T17:47:27.600+0800: [CMS-concurrent-mark: 0.002/0.002 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-05-05T17:47:27.600+0800: [CMS-concurrent-preclean-start]
2021-05-05T17:47:27.600+0800: [CMS-concurrent-preclean: 0.000/0.000 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-05-05T17:47:27.600+0800: [CMS-concurrent-abortable-preclean-start]
2021-05-05T17:47:27.623+0800: [GC (Allocation Failure) 2021-05-05T17:47:27.623+0800: [ParNew: 139656K->17470K(157248K), 0.0091883 secs] 457980K->358400K(506816K), 0.0092664 secs] [Times: user=0.05 sys=0.01, real=0.01 secs] 
2021-05-05T17:47:27.632+0800: [CMS-concurrent-abortable-preclean: 0.001/0.032 secs] [Times: user=0.06 sys=0.01, real=0.03 secs] 
2021-05-05T17:47:27.632+0800: [GC (CMS Final Remark) [YG occupancy: 20519 K (157248 K)]2021-05-05T17:47:27.632+0800: [Rescan (parallel) , 0.0004578 secs]2021-05-05T17:47:27.633+0800: [weak refs processing, 0.0000235 secs]2021-05-05T17:47:27.633+0800: [class unloading, 0.0005131 secs]2021-05-05T17:47:27.633+0800: [scrub symbol table, 0.0009426 secs]2021-05-05T17:47:27.634+0800: [scrub string table, 0.0005783 secs][1 CMS-remark: 340930K(349568K)] 361449K(506816K), 0.0026737 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-05-05T17:47:27.635+0800: [CMS-concurrent-sweep-start]
2021-05-05T17:47:27.636+0800: [CMS-concurrent-sweep: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-05-05T17:47:27.637+0800: [CMS-concurrent-reset-start]
2021-05-05T17:47:27.638+0800: [CMS-concurrent-reset: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-05-05T17:47:27.665+0800: [GC (Allocation Failure) 2021-05-05T17:47:27.665+0800: [ParNew: 157246K->157246K(157248K), 0.0000259 secs]2021-05-05T17:47:27.665+0800: [CMS: 340930K->331310K(349568K), 0.0623774 secs] 498176K->331310K(506816K), [Metaspace: 3508K->3508K(1056768K)], 0.0624892 secs] [Times: user=0.08 sys=0.00, real=0.06 secs] 
2021-05-05T17:47:27.728+0800: [GC (CMS Initial Mark) [1 CMS-initial-mark: 331310K(349568K)] 334371K(506816K), 0.0002018 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-05-05T17:47:27.728+0800: [CMS-concurrent-mark-start]
2021-05-05T17:47:27.731+0800: [CMS-concurrent-mark: 0.002/0.002 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-05-05T17:47:27.731+0800: [CMS-concurrent-preclean-start]
2021-05-05T17:47:27.731+0800: [CMS-concurrent-preclean: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-05-05T17:47:27.731+0800: [CMS-concurrent-abortable-preclean-start]
2021-05-05T17:47:27.763+0800: [GC (Allocation Failure) 2021-05-05T17:47:27.763+0800: [ParNew: 139776K->139776K(157248K), 0.0000288 secs]2021-05-05T17:47:27.763+0800: [CMS2021-05-05T17:47:27.763+0800: [CMS-concurrent-abortable-preclean: 0.001/0.031 secs] [Times: user=0.08 sys=0.02, real=0.03 secs] 
 (concurrent mode failure): 331310K->339969K(349568K), 0.0685659 secs] 471086K->339969K(506816K), [Metaspace: 3899K->3899K(1056768K)], 0.0686803 secs] [Times: user=0.08 sys=0.00, real=0.07 secs] 
2021-05-05T17:47:27.859+0800: [GC (Allocation Failure) 2021-05-05T17:47:27.859+0800: [ParNew: 139776K->139776K(157248K), 0.0000286 secs]2021-05-05T17:47:27.859+0800: [CMS: 339969K->338417K(349568K), 0.0553918 secs] 479745K->338417K(506816K), [Metaspace: 4004K->4004K(1056768K)], 0.0555077 secs] [Times: user=0.06 sys=0.00, real=0.06 secs] 
2021-05-05T17:47:27.915+0800: [GC (CMS Initial Mark) [1 CMS-initial-mark: 338417K(349568K)] 341213K(506816K), 0.0004057 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-05-05T17:47:27.915+0800: [CMS-concurrent-mark-start]
执行结束!共生成对象次数:9549
Heap
 par new generation   total 157248K, used 4194K [0x00000000e0000000, 0x00000000eaaa0000, 0x00000000eaaa0000)
  eden space 139776K,   3% used [0x00000000e0000000, 0x00000000e0418950, 0x00000000e8880000)
  from space 17472K,   0% used [0x00000000e9990000, 0x00000000e9990000, 0x00000000eaaa0000)
  to   space 17472K,   0% used [0x00000000e8880000, 0x00000000e8880000, 0x00000000e9990000)
 concurrent mark-sweep generation total 349568K, used 338417K [0x00000000eaaa0000, 0x0000000100000000, 0x0000000100000000)
 Metaspace       used 4011K, capacity 4572K, committed 4864K, reserved 1056768K
  class space    used 443K, capacity 460K, committed 512K, reserved 1048576K
```

- GC (CMS Initial Mark) 初始标记
- CMS-concurrent-mark 并发标记
- CMS-concurrent-preclean  并发预清理
- GC (CMS Final Remark) 最终标记
- CMS-concurrent-sweep 并发处理
- CMS-concurrent-reset 并发重置

可以看到CMS GC的各个阶段都有体现。并且执行顺序也是不确定的。分阶段处理期间也可以执行 Yong GC



### G1 GC

`java -XX:+UseG1GC -Xms512m -Xmx512m -Xloggc:gc.demo4.log -XX:+PrintGCDetails -XX:+PrintGCDateStamps GCLogAnalysis`

```
[Eden: 21.0M(21.0M)->0.0B(22.0M) Survivors: 4096.0K->3072.0K Heap: 425.2M(512.0M)->417.4M(512.0M)]
 [Times: user=0.00 sys=0.00, real=0.01 secs] 
2021-05-05T17:48:29.468+0800: [GC pause (G1 Humongous Allocation) (young) (initial-mark), 0.0017490 secs]
   [Parallel Time: 1.0 ms, GC Workers: 8]
      [GC Worker Start (ms): Min: 1145.0, Avg: 1145.0, Max: 1145.2, Diff: 0.2]
      [Ext Root Scanning (ms): Min: 0.0, Avg: 0.2, Max: 0.3, Diff: 0.3, Sum: 1.6]
      [Update RS (ms): Min: 0.0, Avg: 0.2, Max: 0.2, Diff: 0.2, Sum: 1.4]
         [Processed Buffers: Min: 1, Avg: 2.0, Max: 3, Diff: 2, Sum: 16]
      [Scan RS (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]
      [Code Root Scanning (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]
      [Object Copy (ms): Min: 0.2, Avg: 0.3, Max: 0.4, Diff: 0.1, Sum: 2.5]
      [Termination (ms): Min: 0.0, Avg: 0.1, Max: 0.2, Diff: 0.2, Sum: 0.9]
         [Termination Attempts: Min: 1, Avg: 1.0, Max: 1, Diff: 0, Sum: 8]
      [GC Worker Other (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.1]
      [GC Worker Total (ms): Min: 0.7, Avg: 0.8, Max: 0.9, Diff: 0.2, Sum: 6.5]
      [GC Worker End (ms): Min: 1145.8, Avg: 1145.8, Max: 1145.8, Diff: 0.0]
   [Code Root Fixup: 0.0 ms]
   [Code Root Purge: 0.0 ms]
   [Clear CT: 0.2 ms]
   [Other: 0.6 ms]
      [Choose CSet: 0.0 ms]
      [Ref Proc: 0.3 ms]
      [Ref Enq: 0.0 ms]
      [Redirty Cards: 0.1 ms]
      [Humongous Register: 0.1 ms]
      [Humongous Reclaim: 0.0 ms]
      [Free CSet: 0.0 ms]
   [Eden: 1024.0K(22.0M)->0.0B(25.0M) Survivors: 3072.0K->0.0B Heap: 417.5M(512.0M)->417.1M(512.0M)]
 [Times: user=0.09 sys=0.00, real=0.00 secs] 
2021-05-05T17:48:29.470+0800: [GC concurrent-root-region-scan-start]
2021-05-05T17:48:29.470+0800: [GC concurrent-root-region-scan-end, 0.0000228 secs]
2021-05-05T17:48:29.470+0800: [GC concurrent-mark-start]
执行结束!共生成对象次数:8555
2021-05-05T17:48:29.475+0800: [GC concurrent-mark-end, 0.0049233 secs]
2021-05-05T17:48:29.475+0800: [GC remark 2021-05-05T17:48:29.475+0800: [Finalize Marking, 0.0003631 secs] 2021-05-05T17:48:29.476+0800: [GC ref-proc, 0.0002395 secs] 2021-05-05T17:48:29.476+0800: [Unloading, 0.0006326 secs], 0.0023017 secs]
 [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-05-05T17:48:29.478+0800: [GC cleanup 430M->430M(512M), 0.0008321 secs]
 [Times: user=0.00 sys=0.00, real=0.00 secs] 
Heap
 garbage-first heap   total 524288K, used 439974K [0x00000000e0000000, 0x00000000e0101000, 0x0000000100000000)
  region size 1024K, 11 young (11264K), 0 survivors (0K)
 Metaspace       used 4011K, capacity 4572K, committed 4864K, reserved 1056768K
  class space    used 443K, capacity 460K, committed 512K, reserved 1048576K
```

因 G1 GC 的日志过于多和复杂，所以我只摘取片段。日志里也可以明显看到 分阶段的体现。

G1 GC 日志也是目前对复杂的日志了。需要借助工具帮助我们分析了。



## 同一GC下不同堆内存配置的表现情况

首先要知道配置堆内存参数时，需要把 Xmx（起始堆内存） 和 Xms（最大堆内存） 的值设为一样（好习惯）。以 JDK8 默认的并行 GC 为例总结一下不同堆内存对于 JVM 以及系统的影响。

### 128m

`java -XX:+UseParallelGC -Xms128m -Xmx128m -Xloggc:gc.demo1.log -XX:+PrintGCDetails -XX:+PrintGCDateStamps GCLogAnalysis`

```
正在执行...
2021-05-05T17:17:11.776+0800: [GC (Allocation Failure) [PSYoungGen: 33060K->5111K(38400K)] 33060K->12053K(125952K), 0.0034717 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-05-05T17:17:11.790+0800: [GC (Allocation Failure) [PSYoungGen: 38224K->5111K(38400K)] 45166K->23178K(125952K), 0.0039854 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-05-05T17:17:11.802+0800: [GC (Allocation Failure) [PSYoungGen: 38223K->5119K(38400K)] 56290K->35345K(125952K), 0.0031662 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-05-05T17:17:11.812+0800: [GC (Allocation Failure) [PSYoungGen: 38281K->5106K(38400K)] 68508K->46327K(125952K), 0.0036614 secs] [Times: user=0.03 sys=0.00, real=0.00 secs] 
2021-05-05T17:17:11.822+0800: [GC (Allocation Failure) [PSYoungGen: 38386K->5116K(38400K)] 79607K->57904K(125952K), 0.0032661 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-05-05T17:17:11.831+0800: [GC (Allocation Failure) [PSYoungGen: 38396K->5115K(19968K)] 91184K->71958K(107520K), 0.0036861 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-05-05T17:17:11.837+0800: [GC (Allocation Failure) [PSYoungGen: 19956K->9825K(29184K)] 86799K->78122K(116736K), 0.0021513 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-05-05T17:17:11.842+0800: [GC (Allocation Failure) [PSYoungGen: 24673K->14333K(29184K)] 92970K->84917K(116736K), 0.0029337 secs] [Times: user=0.06 sys=0.02, real=0.00 secs] 
2021-05-05T17:17:11.845+0800: [Full GC (Ergonomics) [PSYoungGen: 14333K->0K(29184K)] [ParOldGen: 70583K->79388K(87552K)] 84917K->79388K(116736K), [Metaspace: 3508K->3508K(1056768K)], 0.0160036 secs] [Times: user=0.03 sys=0.00, real=0.02 secs] 
2021-05-05T17:17:11.864+0800: [Full GC (Ergonomics) [PSYoungGen: 14699K->0K(29184K)] [ParOldGen: 79388K->83151K(87552K)] 94087K->83151K(116736K), [Metaspace: 3508K->3508K(1056768K)], 0.0158881 secs] [Times: user=0.06 sys=0.02, real=0.02 secs] 
2021-05-05T17:17:11.883+0800: [Full GC (Ergonomics) [PSYoungGen: 14552K->0K(29184K)] [ParOldGen: 83151K->85635K(87552K)] 97704K->85635K(116736K), [Metaspace: 3508K->3508K(1056768K)], 0.0156776 secs] [Times: user=0.05 sys=0.02, real=0.02 secs] 
2021-05-05T17:17:11.901+0800: [Full GC (Ergonomics) [PSYoungGen: 14510K->1863K(29184K)] [ParOldGen: 85635K->87426K(87552K)] 100146K->89289K(116736K), [Metaspace: 3508K->3508K(1056768K)], 0.0135598 secs] [Times: user=0.08 sys=0.00, real=0.01 secs] 
2021-05-05T17:17:11.917+0800: [Full GC (Ergonomics) [PSYoungGen: 14801K->4786K(29184K)] [ParOldGen: 87426K->86841K(87552K)] 102228K->91627K(116736K), [Metaspace: 3508K->3508K(1056768K)], 0.0169425 secs] [Times: user=0.05 sys=0.00, real=0.02 secs] 
2021-05-05T17:17:11.936+0800: [Full GC (Ergonomics) [PSYoungGen: 14517K->7011K(29184K)] [ParOldGen: 86841K->87522K(87552K)] 101359K->94533K(116736K), [Metaspace: 3508K->3508K(1056768K)], 0.0272182 secs] [Times: user=0.02 sys=0.00, real=0.03 secs] 
2021-05-05T17:17:11.965+0800: [Full GC (Ergonomics) [PSYoungGen: 14723K->10302K(29184K)] [ParOldGen: 87522K->87522K(87552K)] 102245K->97824K(116736K), [Metaspace: 3508K->3508K(1056768K)], 0.0040735 secs] [Times: user=0.02 sys=0.02, real=0.00 secs] 
2021-05-05T17:17:11.970+0800: [Full GC (Ergonomics) [PSYoungGen: 14785K->13537K(29184K)] [ParOldGen: 87522K->87112K(87552K)] 102307K->100650K(116736K), [Metaspace: 3508K->3508K(1056768K)], 0.0103131 secs] [Times: user=0.00 sys=0.00, real=0.01 secs] 
2021-05-05T17:17:11.981+0800: [Full GC (Ergonomics) [PSYoungGen: 14544K->13537K(29184K)] [ParOldGen: 87112K->87112K(87552K)] 101656K->100650K(116736K), [Metaspace: 3508K->3508K(1056768K)], 0.0026679 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-05-05T17:17:11.984+0800: [Full GC (Ergonomics) [PSYoungGen: 14543K->13565K(29184K)] [ParOldGen: 87112K->87112K(87552K)] 101655K->100677K(116736K), [Metaspace: 3508K->3508K(1056768K)], 0.0024225 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-05-05T17:17:11.986+0800: [Full GC (Ergonomics) [PSYoungGen: 14826K->14009K(29184K)] [ParOldGen: 87112K->87112K(87552K)] 101938K->101121K(116736K), [Metaspace: 3508K->3508K(1056768K)], 0.0021885 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-05-05T17:17:11.989+0800: [Full GC (Ergonomics) [PSYoungGen: 14618K->14153K(29184K)] [ParOldGen: 87112K->87112K(87552K)] 101730K->101265K(116736K), [Metaspace: 3508K->3508K(1056768K)], 0.0026626 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-05-05T17:17:11.992+0800: [Full GC (Ergonomics) [PSYoungGen: 14777K->14109K(29184K)] [ParOldGen: 87112K->87016K(87552K)] 101889K->101125K(116736K), [Metaspace: 3508K->3508K(1056768K)], 0.0045084 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-05-05T17:17:11.997+0800: [Full GC (Allocation Failure) [PSYoungGen: 14109K->14109K(29184K)] [ParOldGen: 87016K->86990K(87552K)] 101125K->101099K(116736K), [Metaspace: 3508K->3508K(1056768K)], 0.0268633 secs] [Times: user=0.11 sys=0.00, real=0.03 secs] 
```

查看 GC 日志可以发现，先是几次 Yong GC（PSYoungGen）是很正常的，然后就一直出现 Full GC。这是为什么呢？请继续往下看



```
Heap
 PSYoungGen      total 29184K, used 14440K [0x00000000fd580000, 0x0000000100000000, 0x0000000100000000)
  eden space 14848K, 97% used [0x00000000fd580000,0x00000000fe39a338,0x00000000fe400000)
  from space 14336K, 0% used [0x00000000ff200000,0x00000000ff200000,0x0000000100000000)
  to   space 14336K, 0% used [0x00000000fe400000,0x00000000fe400000,0x00000000ff200000)
 ParOldGen       total 87552K, used 86990K [0x00000000f8000000, 0x00000000fd580000, 0x00000000fd580000)
  object space 87552K, 99% used [0x00000000f8000000,0x00000000fd4f3b98,0x00000000fd580000)
 Metaspace       used 3539K, capacity 4502K, committed 4864K, reserved 1056768K
  class space    used 388K, capacity 390K, committed 512K, reserved 1048576K
Exception in thread "main" java.lang.OutOfMemoryError: Java heap space
	at com.geekbang.week02.GCLogAnalysis.generateGarbage(GCLogAnalysis.java:51)
	at com.geekbang.week02.GCLogAnalysis.main(GCLogAnalysis.java:28)
```

查看 Heap（堆内存空间使用情况）可以第一看到有个报错信息 OOM 。什么？尽然内存溢出了。说明了 128m 的最大内存空间是不够用的，需要立马加大 堆内存 的空间。另外也可以看到 eden 的使用率为 97% 和 ParOldGen（老年代）的使用率为 99% 以及元数据区的使用率也是很高的。



### 521m

`java -XX:+UseParallelGC -Xms512m -Xmx512m -Xloggc:gc.demo2.log -XX:+PrintGCDetails -XX:+PrintGCDateStamps GCLogAnalysis`

```
正在执行...
2021-05-05T17:06:27.888+0800: [GC (Allocation Failure) [PSYoungGen: 131584K->21497K(153088K)] 131584K->36776K(502784K), 0.0097991 secs] [Times: user=0.01 sys=0.00, real=0.01 secs] 
2021-05-05T17:06:27.927+0800: [GC (Allocation Failure) [PSYoungGen: 153081K->21501K(153088K)] 168360K->82991K(502784K), 0.0142435 secs] [Times: user=0.02 sys=0.08, real=0.01 secs] 
2021-05-05T17:06:27.962+0800: [GC (Allocation Failure) [PSYoungGen: 153085K->21487K(153088K)] 214575K->122845K(502784K), 0.0128050 secs] [Times: user=0.02 sys=0.06, real=0.01 secs] 
2021-05-05T17:06:27.993+0800: [GC (Allocation Failure) [PSYoungGen: 152908K->21496K(153088K)] 254266K->166913K(502784K), 0.0126067 secs] [Times: user=0.03 sys=0.08, real=0.01 secs] 
2021-05-05T17:06:28.030+0800: [GC (Allocation Failure) [PSYoungGen: 153080K->21503K(153088K)] 298497K->215314K(502784K), 0.0160360 secs] [Times: user=0.09 sys=0.02, real=0.02 secs] 
2021-05-05T17:06:28.070+0800: [GC (Allocation Failure) [PSYoungGen: 153087K->21496K(80384K)] 346898K->254450K(430080K), 0.0119593 secs] [Times: user=0.01 sys=0.09, real=0.01 secs] 
2021-05-05T17:06:28.090+0800: [GC (Allocation Failure) [PSYoungGen: 80376K->38993K(116736K)] 313330K->276331K(466432K), 0.0066605 secs] [Times: user=0.09 sys=0.00, real=0.01 secs] 
2021-05-05T17:06:28.106+0800: [GC (Allocation Failure) [PSYoungGen: 97873K->49670K(116736K)] 335211K->292293K(466432K), 0.0078947 secs] [Times: user=0.09 sys=0.00, real=0.01 secs] 
2021-05-05T17:06:28.124+0800: [GC (Allocation Failure) [PSYoungGen: 108431K->54492K(116736K)] 351054K->306673K(466432K), 0.0112858 secs] [Times: user=0.11 sys=0.00, real=0.01 secs] 
2021-05-05T17:06:28.145+0800: [GC (Allocation Failure) [PSYoungGen: 113372K->36893K(116736K)] 365553K->322193K(466432K), 0.0123122 secs] [Times: user=0.06 sys=0.05, real=0.01 secs] 
2021-05-05T17:06:28.166+0800: [GC (Allocation Failure) [PSYoungGen: 95773K->22838K(116736K)] 381073K->344482K(466432K), 0.0106986 secs] [Times: user=0.05 sys=0.08, real=0.01 secs] 
2021-05-05T17:06:28.177+0800: [Full GC (Ergonomics) [PSYoungGen: 22838K->0K(116736K)] [ParOldGen: 321643K->235707K(349696K)] 344482K->235707K(466432K), [Metaspace: 3509K->3509K(1056768K)], 0.0367732 secs] [Times: user=0.19 sys=0.01, real=0.04 secs] 
2021-05-05T17:06:28.225+0800: [GC (Allocation Failure) [PSYoungGen: 58403K->22768K(116736K)] 294110K->258476K(466432K), 0.0036257 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-05-05T17:06:28.241+0800: [GC (Allocation Failure) [PSYoungGen: 81483K->18810K(116736K)] 317190K->276655K(466432K), 0.0064905 secs] [Times: user=0.00 sys=0.00, real=0.01 secs] 
2021-05-05T17:06:28.259+0800: [GC (Allocation Failure) [PSYoungGen: 77430K->22991K(116736K)] 335274K->299152K(466432K), 0.0062410 secs] [Times: user=0.00 sys=0.00, real=0.01 secs] 
2021-05-05T17:06:28.276+0800: [GC (Allocation Failure) [PSYoungGen: 81871K->22203K(116736K)] 358032K->319717K(466432K), 0.0065424 secs] [Times: user=0.08 sys=0.00, real=0.01 secs] 
2021-05-05T17:06:28.283+0800: [Full GC (Ergonomics) [PSYoungGen: 22203K->0K(116736K)] [ParOldGen: 297514K->266022K(349696K)] 319717K->266022K(466432K), [Metaspace: 3509K->3509K(1056768K)], 0.0491804 secs] [Times: user=0.17 sys=0.00, real=0.05 secs] 
2021-05-05T17:06:28.345+0800: [GC (Allocation Failure) [PSYoungGen: 58835K->17995K(116736K)] 324858K->284017K(466432K), 0.0035668 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-05-05T17:06:28.361+0800: [GC (Allocation Failure) [PSYoungGen: 76875K->18384K(116736K)] 342897K->300550K(466432K), 0.0068849 secs] [Times: user=0.00 sys=0.00, real=0.01 secs] 
2021-05-05T17:06:28.382+0800: [GC (Allocation Failure) [PSYoungGen: 77264K->22093K(116736K)] 359430K->320683K(466432K), 0.0069151 secs] [Times: user=0.00 sys=0.00, real=0.01 secs] 
2021-05-05T17:06:28.399+0800: [GC (Allocation Failure) [PSYoungGen: 80973K->16813K(116736K)] 379563K->337103K(466432K), 0.0075169 secs] [Times: user=0.05 sys=0.00, real=0.01 secs] 
2021-05-05T17:06:28.407+0800: [Full GC (Ergonomics) [PSYoungGen: 16813K->0K(116736K)] [ParOldGen: 320289K->286142K(349696K)] 337103K->286142K(466432K), [Metaspace: 3509K->3509K(1056768K)], 0.0516025 secs] [Times: user=0.14 sys=0.02, real=0.05 secs] 
2021-05-05T17:06:28.471+0800: [GC (Allocation Failure) [PSYoungGen: 58872K->19014K(116736K)] 345015K->305156K(466432K), 0.0034642 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-05-05T17:06:28.486+0800: [GC (Allocation Failure) [PSYoungGen: 77379K->19972K(116736K)] 363521K->324453K(466432K), 0.0061794 secs] [Times: user=0.00 sys=0.00, real=0.01 secs] 
2021-05-05T17:06:28.508+0800: [GC (Allocation Failure) [PSYoungGen: 78626K->19203K(116736K)] 383107K->342891K(466432K), 0.0081925 secs] [Times: user=0.02 sys=0.01, real=0.01 secs] 
2021-05-05T17:06:28.516+0800: [Full GC (Ergonomics) [PSYoungGen: 19203K->0K(116736K)] [ParOldGen: 323688K->301992K(349696K)] 342891K->301992K(466432K), [Metaspace: 3509K->3509K(1056768K)], 0.0550711 secs] [Times: user=0.23 sys=0.00, real=0.06 secs] 
2021-05-05T17:06:28.583+0800: [GC (Allocation Failure) [PSYoungGen: 58740K->21297K(116736K)] 360733K->323290K(466432K), 0.0075603 secs] [Times: user=0.00 sys=0.00, real=0.01 secs] 
2021-05-05T17:06:28.605+0800: [GC (Allocation Failure) [PSYoungGen: 80177K->19606K(116736K)] 382170K->341850K(466432K), 0.0077314 secs] [Times: user=0.06 sys=0.00, real=0.01 secs] 
2021-05-05T17:06:28.613+0800: [Full GC (Ergonomics) [PSYoungGen: 19606K->0K(116736K)] [ParOldGen: 322243K->310142K(349696K)] 341850K->310142K(466432K), [Metaspace: 3509K->3509K(1056768K)], 0.0555550 secs] [Times: user=0.19 sys=0.00, real=0.06 secs] 
2021-05-05T17:06:28.681+0800: [GC (Allocation Failure) [PSYoungGen: 58251K->22771K(116736K)] 368393K->332913K(466432K), 0.0053735 secs] [Times: user=0.00 sys=0.00, real=0.01 secs] 
2021-05-05T17:06:28.705+0800: [GC (Allocation Failure) [PSYoungGen: 81651K->20695K(116736K)] 391793K->353516K(466432K), 0.0093876 secs] [Times: user=0.00 sys=0.00, real=0.01 secs] 
2021-05-05T17:06:28.715+0800: [Full GC (Ergonomics) [PSYoungGen: 20695K->0K(116736K)] [ParOldGen: 332821K->322643K(349696K)] 353516K->322643K(466432K), [Metaspace: 3509K->3509K(1056768K)], 0.0611294 secs] [Times: user=0.22 sys=0.00, real=0.06 secs] 
2021-05-05T17:06:28.789+0800: [Full GC (Ergonomics) [PSYoungGen: 58303K->0K(116736K)] [ParOldGen: 322643K->328215K(349696K)] 380946K->328215K(466432K), [Metaspace: 3644K->3644K(1056768K)], 0.0587502 secs] [Times: user=0.30 sys=0.00, real=0.06 secs] 
执行结束!共生成对象次数:7779
```

查看 GC 日志可以发现，有 Yong GC（PSYoungGen）和 Full GC 。刚开始先是几次 Yong GC，然后 Full GC 出现。

可以符合我们的预期，Yong GC 的出现就是说明年轻代中空间不够用了。经过几次 Yong GC 之后，有些对象（数据），从年轻代晋升到老年代了。然后过了一段时间之后 老年代的空间也不够用了，就出现了 Full GC。经过几次这样的 GC 最后程序结束了。



```
Heap
 PSYoungGen      total 116736K, used 2450K [0x00000000f5580000, 0x0000000100000000, 0x0000000100000000)
  eden space 58880K, 4% used [0x00000000f5580000,0x00000000f57e4b18,0x00000000f8f00000)
  from space 57856K, 0% used [0x00000000fc780000,0x00000000fc780000,0x0000000100000000)
  to   space 57856K, 0% used [0x00000000f8f00000,0x00000000f8f00000,0x00000000fc780000)
 ParOldGen       total 349696K, used 328215K [0x00000000e0000000, 0x00000000f5580000, 0x00000000f5580000)
  object space 349696K, 93% used [0x00000000e0000000,0x00000000f4085fb0,0x00000000f5580000)
 Metaspace       used 3652K, capacity 4540K, committed 4864K, reserved 1056768K
  class space    used 397K, capacity 428K, committed 512K, reserved 1048576K
```

查看 Heap （堆内存空间使用情况）可以发现， 老年代的使用率居然高达 93%。元数据空间使用率也是很高的。



### 1g

`java -XX:+UseParallelGC -Xms1g -Xmx1g -Xloggc:gc.demo3.log -XX:+PrintGCDetails -XX:+PrintGCDateStamps GCLogAnalysis`

```
正在执行...
2021-05-05T17:24:24.153+0800: [GC (Allocation Failure) [PSYoungGen: 262144K->43512K(305664K)] 262144K->73403K(1005056K), 0.0128141 secs] [Times: user=0.03 sys=0.08, real=0.01 secs] 
2021-05-05T17:24:24.209+0800: [GC (Allocation Failure) [PSYoungGen: 305656K->43504K(305664K)] 335547K->137444K(1005056K), 0.0218829 secs] [Times: user=0.03 sys=0.08, real=0.02 secs] 
2021-05-05T17:24:24.275+0800: [GC (Allocation Failure) [PSYoungGen: 305648K->43509K(305664K)] 399588K->211172K(1005056K), 0.0175654 secs] [Times: user=0.01 sys=0.09, real=0.02 secs] 
2021-05-05T17:24:24.329+0800: [GC (Allocation Failure) [PSYoungGen: 305530K->43517K(305664K)] 473193K->282501K(1005056K), 0.0183432 secs] [Times: user=0.06 sys=0.05, real=0.02 secs] 
2021-05-05T17:24:24.384+0800: [GC (Allocation Failure) [PSYoungGen: 305661K->43516K(305664K)] 544645K->360219K(1005056K), 0.0191717 secs] [Times: user=0.11 sys=0.00, real=0.02 secs] 
2021-05-05T17:24:24.441+0800: [GC (Allocation Failure) [PSYoungGen: 305660K->43519K(160256K)] 622363K->431399K(859648K), 0.0205968 secs] [Times: user=0.02 sys=0.08, real=0.02 secs] 
2021-05-05T17:24:24.479+0800: [GC (Allocation Failure) [PSYoungGen: 160255K->63453K(232960K)] 548135K->459377K(932352K), 0.0108389 secs] [Times: user=0.06 sys=0.03, real=0.01 secs] 
2021-05-05T17:24:24.509+0800: [GC (Allocation Failure) [PSYoungGen: 180189K->84130K(232960K)] 576113K->488817K(932352K), 0.0140609 secs] [Times: user=0.09 sys=0.00, real=0.01 secs] 
2021-05-05T17:24:24.538+0800: [GC (Allocation Failure) [PSYoungGen: 200866K->99306K(232960K)] 605553K->519222K(932352K), 0.0159562 secs] [Times: user=0.09 sys=0.00, real=0.02 secs] 
2021-05-05T17:24:24.575+0800: [GC (Allocation Failure) [PSYoungGen: 216042K->74657K(232960K)] 635958K->548330K(932352K), 0.0191734 secs] [Times: user=0.00 sys=0.19, real=0.02 secs] 
2021-05-05T17:24:24.611+0800: [GC (Allocation Failure) [PSYoungGen: 191393K->38163K(232960K)] 665066K->580407K(932352K), 0.0169492 secs] [Times: user=0.06 sys=0.03, real=0.02 secs] 
2021-05-05T17:24:24.651+0800: [GC (Allocation Failure) [PSYoungGen: 154712K->36468K(232960K)] 696956K->613746K(932352K), 0.0116348 secs] [Times: user=0.06 sys=0.03, real=0.01 secs] 
2021-05-05T17:24:24.680+0800: [GC (Allocation Failure) [PSYoungGen: 153166K->38699K(232960K)] 730445K->647113K(932352K), 0.0147195 secs] [Times: user=0.06 sys=0.01, real=0.01 secs] 
2021-05-05T17:24:24.695+0800: [Full GC (Ergonomics) [PSYoungGen: 38699K->0K(232960K)] [ParOldGen: 608413K->325963K(699392K)] 647113K->325963K(932352K), [Metaspace: 3508K->3508K(1056768K)], 0.0625295 secs] [Times: user=0.34 sys=0.03, real=0.06 secs] 
2021-05-05T17:24:24.776+0800: [GC (Allocation Failure) [PSYoungGen: 116736K->41304K(232960K)] 442699K->367267K(932352K), 0.0063804 secs] [Times: user=0.11 sys=0.00, real=0.01 secs] 
2021-05-05T17:24:24.797+0800: [GC (Allocation Failure) [PSYoungGen: 158040K->38069K(232960K)] 484003K->400880K(932352K), 0.0102936 secs] [Times: user=0.00 sys=0.00, real=0.01 secs] 
2021-05-05T17:24:24.826+0800: [GC (Allocation Failure) [PSYoungGen: 154805K->38467K(232960K)] 517616K->435133K(932352K), 0.0097443 secs] [Times: user=0.09 sys=0.00, real=0.01 secs] 
2021-05-05T17:24:24.854+0800: [GC (Allocation Failure) [PSYoungGen: 155203K->44780K(232960K)] 551869K->475421K(932352K), 0.0127941 secs] [Times: user=0.08 sys=0.00, real=0.01 secs] 
2021-05-05T17:24:24.884+0800: [GC (Allocation Failure) [PSYoungGen: 161516K->38455K(232960K)] 592157K->509778K(932352K), 0.0110082 secs] [Times: user=0.11 sys=0.00, real=0.01 secs] 
2021-05-05T17:24:24.918+0800: [GC (Allocation Failure) [PSYoungGen: 155191K->33920K(232960K)] 626514K->538497K(932352K), 0.0108030 secs] [Times: user=0.09 sys=0.00, real=0.01 secs] 
2021-05-05T17:24:24.956+0800: [GC (Allocation Failure) [PSYoungGen: 150095K->35682K(232960K)] 654672K->570183K(932352K), 0.0098287 secs] [Times: user=0.00 sys=0.00, real=0.01 secs] 
2021-05-05T17:24:24.987+0800: [GC (Allocation Failure) [PSYoungGen: 152418K->35980K(232960K)] 686919K->603911K(932352K), 0.0093167 secs] [Times: user=0.00 sys=0.00, real=0.01 secs] 
执行结束!共生成对象次数:12780
```

查看 GC 日志可以看到整体 GC 次数明显比 512m 的时候要少了很多。并且出现 Full GC 的次数也少了很多。



```
Heap
 PSYoungGen      total 232960K, used 112523K [0x00000000eab00000, 0x0000000100000000, 0x0000000100000000)
  eden space 116736K, 65% used [0x00000000eab00000,0x00000000ef5bfd68,0x00000000f1d00000)
  from space 116224K, 30% used [0x00000000f1d00000,0x00000000f40231d8,0x00000000f8e80000)
  to   space 116224K, 0% used [0x00000000f8e80000,0x00000000f8e80000,0x0000000100000000)
 ParOldGen       total 699392K, used 567930K [0x00000000c0000000, 0x00000000eab00000, 0x00000000eab00000)
  object space 699392K, 81% used [0x00000000c0000000,0x00000000e2a9eaa0,0x00000000eab00000)
 Metaspace       used 4010K, capacity 4572K, committed 4864K, reserved 1056768K
  class space    used 443K, capacity 460K, committed 512K, reserved 1048576K
```

查看 Heap（堆内存空间使用情况）可以看到 eden 的使用率为 65% 和 ParOldGen（老年代）的使用率为 81% 以及元数据区的使用率也相对较高。可以大体得出结论知道现在这个 内存配置 还是会有OOM 的风险的。



### 2g

`java -XX:+UseParallelGC -Xms2g -Xmx2g -Xloggc:gc.demo4.log -XX:+PrintGCDetails -XX:+PrintGCDateStamps GCLogAnalysis`

```
正在执行...
2021-05-05T17:28:56.780+0800: [GC (Allocation Failure) [PSYoungGen: 524800K->87031K(611840K)] 524800K->144366K(2010112K), 0.0239684 secs] [Times: user=0.06 sys=0.14, real=0.02 secs] 
2021-05-05T17:28:56.885+0800: [GC (Allocation Failure) [PSYoungGen: 611831K->87039K(611840K)] 669166K->259314K(2010112K), 0.0335034 secs] [Times: user=0.09 sys=0.13, real=0.03 secs] 
2021-05-05T17:28:56.998+0800: [GC (Allocation Failure) [PSYoungGen: 611839K->87028K(611840K)] 784114K->371469K(2010112K), 0.0363600 secs] [Times: user=0.19 sys=0.09, real=0.04 secs] 
2021-05-05T17:28:57.110+0800: [GC (Allocation Failure) [PSYoungGen: 611828K->87036K(611840K)] 896269K->475783K(2010112K), 0.0367291 secs] [Times: user=0.05 sys=0.13, real=0.04 secs] 
2021-05-05T17:28:57.216+0800: [GC (Allocation Failure) [PSYoungGen: 611836K->87038K(611840K)] 1000583K->594928K(2010112K), 0.0457235 secs] [Times: user=0.08 sys=0.16, real=0.05 secs] 
2021-05-05T17:28:57.331+0800: [GC (Allocation Failure) [PSYoungGen: 611838K->87029K(320000K)] 1119728K->713243K(1718272K), 0.0493551 secs] [Times: user=0.09 sys=0.09, real=0.05 secs] 
2021-05-05T17:28:57.426+0800: [GC (Allocation Failure) [PSYoungGen: 319989K->129212K(465920K)] 946203K->761625K(1864192K), 0.0204202 secs] [Times: user=0.09 sys=0.00, real=0.02 secs] 
2021-05-05T17:28:57.477+0800: [GC (Allocation Failure) [PSYoungGen: 362172K->168217K(465920K)] 994585K->811118K(1864192K), 0.0270384 secs] [Times: user=0.11 sys=0.06, real=0.03 secs] 
执行结束!共生成对象次数:13664
```

查看 GC 日志，可以看到 GC 出现的次数更少， 这次甚至都没有出现 Full GC。

```
Heap
 PSYoungGen      total 465920K, used 175487K [0x00000000d5580000, 0x0000000100000000, 0x0000000100000000)
  eden space 232960K, 3% used [0x00000000d5580000,0x00000000d5c996d0,0x00000000e3900000)
  from space 232960K, 72% used [0x00000000f1c80000,0x00000000fc0c65e8,0x0000000100000000)
  to   space 232960K, 0% used [0x00000000e3900000,0x00000000e3900000,0x00000000f1c80000)
 ParOldGen       total 1398272K, used 642900K [0x0000000080000000, 0x00000000d5580000, 0x00000000d5580000)
  object space 1398272K, 45% used [0x0000000080000000,0x00000000a73d5298,0x00000000d5580000)
 Metaspace       used 4010K, capacity 4572K, committed 4864K, reserved 1056768K
  class space    used 443K, capacity 460K, committed 512K, reserved 1048576K
```

查看 Heap （堆内存空间使用情况）看到 eden 的使用率为 3% ；from 的使用率为 72%；ParOldGen（老年代）的使用率为 45%；元数据空间使用率也不高。

我个人认为这次的内存配置要比 1g 要好，如果我选的话可能会选择  2g 的配置。



### 4g

`java -XX:+UseParallelGC -Xms4g -Xmx4g -Xloggc:gc.demo5.log -XX:+PrintGCDetails -XX:+PrintGCDateStamps GCLogAnalysis`

```
正在执行...
2021-05-05T17:32:16.651+0800: [GC (Allocation Failure) [PSYoungGen: 1048576K->174575K(1223168K)] 1048576K->236290K(4019712K), 0.0415752 secs] [Times: user=0.09 sys=0.19, real=0.04 secs] 
2021-05-05T17:32:16.849+0800: [GC (Allocation Failure) [PSYoungGen: 1223151K->174586K(1223168K)] 1284866K->365602K(4019712K), 0.0707574 secs] [Times: user=0.14 sys=0.20, real=0.07 secs] 
2021-05-05T17:32:17.088+0800: [GC (Allocation Failure) [PSYoungGen: 1223162K->174583K(1223168K)] 1414178K->495227K(4019712K), 0.0511536 secs] [Times: user=0.27 sys=0.06, real=0.05 secs] 
执行结束!共生成对象次数:11536
```

查看 GC  日志发现 ， GC 次数又少了；而且也没有出现  Full GC。



```
Heap
 PSYoungGen      total 1223168K, used 206014K [0x000000076ab00000, 0x00000007c0000000, 0x00000007c0000000)
  eden space 1048576K, 2% used [0x000000076ab00000,0x000000076c9b1d50,0x00000007aab00000)
  from space 174592K, 99% used [0x00000007aab00000,0x00000007b557dd70,0x00000007b5580000)
  to   space 174592K, 0% used [0x00000007b5580000,0x00000007b5580000,0x00000007c0000000)
 ParOldGen       total 2796544K, used 320643K [0x00000006c0000000, 0x000000076ab00000, 0x000000076ab00000)
  object space 2796544K, 11% used [0x00000006c0000000,0x00000006d3920f70,0x000000076ab00000)
 Metaspace       used 4011K, capacity 4572K, committed 4864K, reserved 1056768K
  class space    used 443K, capacity 460K, committed 512K, reserved 1048576K
```

查看 Heap（堆内存空间使用情况），可以看到 eden 的使用率为 2%；from 的使用率为 99%；ParOldGen（老年代）的使用率为11%；元数据区的使用率基本没变。