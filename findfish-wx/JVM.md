## JVM

### 运行时数据区域

1. 程序计数器：程序计数器是一块较小的内存空间，它可以看作是当前线程所执行的字节码的行号指示器。在虚拟机的概念模型里，字节码解释器工作时就是通过改变这个计数器的值来选取下一条需要执行的字节码指令，分支、循环、跳转、异常处理、线程恢复等基础功能都需要依赖这个计数器来完成。是线程私有”的内存。
2. Java虚拟机栈：与程序计数器一样，Java虚拟机栈（Java Virtual Machine Stacks）也是线程私有的，它的生命周期与线程相同。虚拟机栈描述的是Java方法执行的内存模型：每个方法在执行的同时都会创建一个栈帧 ，用于存储局部变量表、操作数栈、动态链接、方法出口等信息。每一个方法从调用直至执行完成的过程，就对应着一个栈帧在虚拟机栈中入栈到出栈的过程。
3. 本地方法栈：本地方法栈（Native Method Stack）与虚拟机栈所发挥的作用是非常相似的，它们之间的区别不过是虚拟机栈为虚拟机执行Java方法（也就是字节码）服务，而本地方法栈则为虚拟机使用到的Native方法服务。
4. Java堆：对于大多数应用来说，Java堆是Java虚拟机所管理的内存中最大的一块。Java堆是被所有线程共享的一块内存区域，在虚拟机启动时创建。此内存区域的唯一目的就是存放对象实例，几乎所有的对象实例都在这里分配内存。
5. 方法区：方法区用于存储已被虚拟机加载的类信息、常量、静态变量，如static修饰的变量加载类的时候就被加载到方法区中。运行时常量池是方法区的一部分，class文件除了有类的字段、接口、方法等描述信息之外，还有常量池用于存放编译期间生成的各种字面量和符号引用。在老版jdk，方法区也被称为永久代。在1.8之后，由于永久代内存经常不够用或发生内存泄露，爆出异常java.lang.OutOfMemoryError，所以在1.8之后废弃永久代，引入元空间的概念。元空间是方法区的在HotSpot jvm 中的实现，元空间的本质和永久代类似，都是对JVM规范中方法区的实现。不过元空间与永久代之间最大的区别在于：元空间并不在虚拟机中，而是使用本地内存。理论上取决于32位/64位系统可虚拟的内存大小。可见也不是无限制的，需要配置参数。

### 分代回收

HotSpot JVM把年轻代分为了三部分：1个Eden区和2个Survivor区（分别叫from和to）。一般情况下，新创建的对象都会被分配到Eden区(一些大对象特殊处理),这些对象经过第一次Minor GC后，如果仍然存活，将会被移到Survivor区。对象在Survivor区中每熬过一次Minor GC，年龄就会增加1岁，当它的年龄增加到一定程度时，就会被移动到年老代中。

因为年轻代中的对象基本都是朝生夕死的，所以在年轻代的垃圾回收算法使用的是复制算法，复制算法的基本思想就是将内存分为两块，每次只用其中一块，当这一块内存用完，就将还活着的对象复制到另外一块上面。复制算法不会产生内存碎片。

在GC开始的时候，对象只会存在于Eden区和名为“From”的Survivor区，Survivor区“To”是空的。紧接着进行GC，Eden区中所有存活的对象都会被复制到“To”，而在“From”区中，仍存活的对象会根据他们的年龄值来决定去向。年龄达到一定值(年龄阈值，可以通过-XX:MaxTenuringThreshold来设置)的对象会被移动到年老代中，没有达到阈值的对象会被复制到“To”区域。经过这次GC后，Eden区和From区已经被清空。这个时候，“From”和“To”会交换他们的角色，也就是新的“To”就是上次GC前的“From”，新的“From”就是上次GC前的“To”。不管怎样，都会保证名为To的Survivor区域是空的。Minor GC会一直重复这样的过程，直到“To”区被填满，“To”区被填满之后，会将所有对象移动到年老代中。

### 动态年龄计算

Hotspot在遍历所有对象时，按照年龄从小到大对其所占用的大小进行累积，当累积的某个年龄大小超过了survivor区的一半时，取这个年龄和MaxTenuringThreshold中更小的一个值，作为新的晋升年龄阈值。

JVM引入动态年龄计算，主要基于如下两点考虑：

1. 如果固定按照MaxTenuringThreshold设定的阈值作为晋升条件： a）MaxTenuringThreshold设置的过大，原本应该晋升的对象一直停留在Survivor区，直到Survivor区溢出，一旦溢出发生，Eden+Svuvivor中对象将不再依据年龄全部提升到老年代，这样对象老化的机制就失效了。 b）MaxTenuringThreshold设置的过小，“过早晋升”即对象不能在新生代充分被回收，大量短期对象被晋升到老年代，老年代空间迅速增长，引起频繁的Major GC。分代回收失去了意义，严重影响GC性能。
2. 相同应用在不同时间的表现不同：特殊任务的执行或者流量成分的变化，都会导致对象的生命周期分布发生波动，那么固定的阈值设定，因为无法动态适应变化，会造成和上面相同的问题。


### 常见的垃圾回收机制

1. 引用计数法：引用计数法是一种简单但速度很慢的垃圾回收技术。每个对象都含有一个引用计数器,当有引用连接至对象时,引用计数加1。当引用离开作用域或被置为null时,引用计数减1。虽然管理引用计数的开销不大,但这项开销在整个程序生命周期中将持续发生。垃圾回收器会在含有全部对象的列表上遍历,当发现某个对象引用计数为0时,就释放其占用的空间。
2. 可达性分析算法：这个算法的基本思路就是通过一系列的称为“GC Roots”的对象作为起始点，从这些节点开始向下搜索，搜索所走过的路径称为引用链，当一个对象到GC Roots没有任何引用链相连（用图论的话来说，就是从GC Roots到这个对象不可达）时，则证明此对象是不可用的。

### CMS的执行过程

1. 初始标记(STW initial mark)：这个过程从垃圾回收的"根对象"开始，只扫描到能够和"根对象"直接关联的对象，并作标记。所以这个过程虽然暂停了整个JVM，但是很快就完成了。
2. 并发标记(Concurrent marking)：这个阶段紧随初始标记阶段，在初始标记的基础上继续向下追溯标记。并发标记阶段，应用程序的线程和并发标记的线程并发执行，所以用户不会感受到停顿。
3. 并发预清理(Concurrent precleaning)：并发预清理阶段仍然是并发的。在这个阶段，虚拟机查找在执行并发标记阶段新进入老年代的对象(可能会有一些对象从新生代晋升到老年代， 或者有一些对象被分配到老年代)。通过重新扫描，减少下一个阶段"重新标记"的工作，因为下一个阶段会Stop The World。
4. 重新标记(STW remark)：这个阶段会暂停虚拟机，收集器线程扫描在CMS堆中剩余的对象。扫描从"跟对象"开始向下追溯，并处理对象关联。
5. 并发清理(Concurrent sweeping)：清理垃圾对象，这个阶段收集器线程和应用程序线程并发执行。
6. 并发重置(Concurrent reset)：这个阶段，重置CMS收集器的数据结构状态，等待下一次垃圾回收。

### G1的执行过程

1. 标记阶段：首先是初始标记(Initial-Mark),这个阶段也是停顿的(stop-the-word)，并且会稍带触发一次yong GC。
2. 并发标记：这个过程在整个堆中进行，并且和应用程序并发运行。并发标记过程可能被yong GC中断。在并发标记阶段，如果发现区域对象中的所有对象都是垃圾，那个这个区域会被立即回收(图中打X)。同时，并发标记过程中，每个区域的对象活性(区域中存活对象的比例)被计算。
3. 再标记：这个阶段是用来补充收集并发标记阶段产新的新垃圾。与之不同的是，G1中采用了更快的算法:SATB。
4. 清理阶段：选择活性低的区域(同时考虑停顿时间)，等待下次yong GC一起收集，这个过程也会有停顿(STW)。
5. 回收/完成：新的yong GC清理被计算好的区域。但是有一些区域还是可能存在垃圾对象，可能是这些区域中对象活性较高，回收不划算，也肯能是为了迎合用户设置的时间，不得不舍弃一些区域的收集。

### G1和CMS的比较

1. CMS收集器是获取最短回收停顿时间为目标的收集器，因为CMS工作时，GC工作线程与用户线程可以并发执行，以此来达到降低停顿时间的目的（只有初始标记和重新标记会STW）。但是CMS收集器对CPU资源非常敏感。在并发阶段，虽然不会导致用户线程停顿，但是会占用CPU资源而导致引用程序变慢，总吞吐量下降。
2. CMS仅作用于老年代，是基于标记清除算法，所以清理的过程中会有大量的空间碎片。
3. CMS收集器无法处理浮动垃圾，由于CMS并发清理阶段用户线程还在运行，伴随程序的运行自热会有新的垃圾不断产生，这一部分垃圾出现在标记过程之后，CMS无法在本次收集中处理它们，只好留待下一次GC时将其清理掉。
4. G1是一款面向服务端应用的垃圾收集器，适用于多核处理器、大内存容量的服务端系统。G1能充分利用CPU、多核环境下的硬件优势，使用多个CPU（CPU或者CPU核心）来缩短STW的停顿时间，它满足短时间停顿的同时达到一个高的吞吐量。
5. 从JDK 9开始，G1成为默认的垃圾回收器。当应用有以下任何一种特性时非常适合用G1：Full GC持续时间太长或者太频繁；对象的创建速率和存活率变动很大；应用不希望停顿时间长(长于0.5s甚至1s)。
6. G1将空间划分成很多块（Region），然后他们各自进行回收。堆比较大的时候可以采用，采用复制算法，碎片化问题不严重。整体上看属于标记整理算法,局部(region之间)属于复制算法。
7. G1 需要记忆集来记录新生代和老年代之间的引用关系，这种数据结构在 G1 中需要占用大量的内存，可能达到整个堆内存容量的 20% 甚至更多。而且 G1 中维护记忆集的成本较高，带来了更高的执行负载，影响效率。所以 CMS 在小内存应用上的表现要优于 G1，而大内存应用上 G1 更有优势，大小内存的界限是6GB到8GB。（Card Table（CMS中）的结构是一个连续的byte[]数组，扫描Card Table的时间比扫描整个老年代的代价要小很多！G1也参照了这个思路，不过采用了一种新的数据结构 Remembered Set 简称Rset。RSet记录了其他Region中的对象引用本Region中对象的关系，属于points-into结构（谁引用了我的对象）。而Card Table则是一种points-out（我引用了谁的对象）的结构，每个Card 覆盖一定范围的Heap（一般为512Bytes）。G1的RSet是在Card Table的基础上实现的：每个Region会记录下别的Region有指向自己的指针，并标记这些指针分别在哪些Card的范围内。 这个RSet其实是一个Hash Table，Key是别的Region的起始地址，Value是一个集合，里面的元素是Card Table的Index。每个Region都有一个对应的Rset。）

### 哪些对象可以作为GC Roots

1. 虚拟机栈（栈帧中的本地变量表）中引用的对象。
2. 方法区中类静态属性引用的对象。
3. 方法区中常量引用的对象。
4. 本地方法栈中JNI（即一般说的Native方法）引用的对象。

### GC中Stop the world（STW）

在执行垃圾收集算法时，Java应用程序的其他所有除了垃圾收集收集器线程之外的线程都被挂起。此时，系统只能允许GC线程进行运行，其他线程则会全部暂停，等待GC线程执行完毕后才能再次运行。这些工作都是由虚拟机在后台自动发起和自动完成的，是在用户不可见的情况下把用户正常工作的线程全部停下来，这对于很多的应用程序，尤其是那些对于实时性要求很高的程序来说是难以接受的。

但不是说GC必须STW,你也可以选择降低运行速度但是可以并发执行的收集算法，这取决于你的业务。

### 垃圾回收算法

1. 停止-复制：先暂停程序的运行,然后将所有存活的对象从当前堆复制到另一个堆,没有被复制的对象全部都是垃圾。当对象被复制到新堆时,它们是一个挨着一个的,所以新堆保持紧凑排列,然后就可以按前述方法简单,直接的分配了。缺点是一浪费空间,两个堆之间要来回倒腾,二是当程序进入稳定态时,可能只会产生极少的垃圾,甚至不产生垃圾,尽管如此,复制式回收器仍会将所有内存自一处复制到另一处。
2. 标记-清除：同样是从堆栈和静态存储区出发,遍历所有的引用,进而找出所有存活的对象。每当它找到一个存活的对象,就会给对象一个标记,这个过程中不会回收任何对象。只有全部标记工作完成的时候,清理动作才会开始。在清理过程中,没有标记的对象会被释放,不会发生任何复制动作。所以剩下的堆空间是不连续的,垃圾回收器如果要希望得到连续空间的话,就得重新整理剩下的对象。
3. 标记-整理：它的第一个阶段与标记/清除算法是一模一样的，均是遍历GC Roots，然后将存活的对象标记。移动所有存活的对象，且按照内存地址次序依次排列，然后将末端内存地址以后的内存全部回收。因此，第二阶段才称为整理阶段。
4. 分代收集算法：把Java堆分为新生代和老年代，然后根据各个年代的特点采用最合适的收集算法。新生代中，对象的存活率比较低，所以选用复制算法，老年代中对象存活率高且没有额外空间对它进行分配担保，所以使用“标记-清除”或“标记-整理”算法进行回收。

### Minor GC和Full GC触发条件

* Minor GC触发条件：当Eden区满时，触发Minor GC。
* Full GC触发条件：
    1. 调用System.gc时，系统建议执行Full GC，但是不必然执行
    2. 老年代空间不足
    3. 方法区空间不足
    4. 通过Minor GC后进入老年代的平均大小大于老年代的可用内存
    5. 由Eden区、From Space区向To Space区复制时，对象大小大于To Space可用内存，则把该对象转存到老年代，且老年代的可用内存小于该对象大小

### 对象什么时候进入老年代

1. 大对象直接进入老年代。 虚拟机提供了一个阈值参数，令大于这个设置值的对象直接在老年代中分配。如果大对象进入新生代，新生代采用的复制算法收集内存，会导致在Eden区和两个Survivor区之间发生大量的内存复制，应该避免这种情况。
2. 长期存活的对象进入老年代。 虚拟机给每个对象定义了一个年龄计数器，对象在Eden区出生，经过一次Minor GC后仍然存活，并且能被Survivor区容纳的话，将被移动到Survivor区中，此时对象年龄设为1。然后对象在Survivor区中每熬过一次 Minor GC，年龄就增加1，当年龄超过设定的阈值时，就会被移动到老年代中。
3. 动态对象年龄判定： 如果在 Survivor 空间中所有相同年龄的对象，大小总和大于 Survivor 空间的一半，那么年龄大于或等于该年龄的对象就直接进入老年代，无须等到阈值中要求的年龄。
4. 空间分配担保： 如果老年代中最大可用的连续空间大于新生代所有对象的总空间，那么 Minor GC 是安全的。如果老年代中最大可用的连续空间大于历代晋升到老年代的对象的平均大小，就进行一次有风险的 Minor GC，如果小于平均值，就进行 Full GC 来让老年代腾出更多的空间。因为新生代使用的是复制算法，为了内存利用率，只使用其中一个 Survivor 空间来做轮换备份，因此如果大量对象在 Minor GC 后仍然存活，导致 Survivor 空间不够用，就会通过分配担保机制，将多出来的对象提前转到老年代，但老年代要进行担保的前提是自己本身还有容纳这些对象的剩余空间，由于无法提前知道会有多少对象存活下来，所以取之前每次晋升到老年代的对象的平均大小作为经验值，与老年代的剩余空间做比较。

### TLAB

在Java中，典型的对象不再堆上分配的情况有两种：TLAB和栈上分配（通过逃逸分析）。JVM在内存新生代Eden Space中开辟了一小块线程私有的区域，称作TLAB（Thread-local allocation buffer）。默认设定为占用Eden Space的1%。在Java程序中很多对象都是小对象且用过即丢，它们不存在线程共享也适合被快速GC，所以对于小对象通常JVM会优先分配在TLAB上，并且TLAB上的分配由于是线程私有所以没有锁开销。因此在实践中分配多个小对象的效率通常比分配一个大对象的效率要高。也就是说，Java中每个线程都会有自己的缓冲区称作TLAB（Thread-local allocation buffer），每个TLAB都只有一个线程可以操作，TLAB结合bump-the-pointer技术可以实现快速的对象分配，而不需要任何的锁进行同步，也就是说，在对象分配的时候不用锁住整个堆，而只需要在自己的缓冲区分配即可。

### Java对象分配的过程

1. 编译器通过逃逸分析，确定对象是在栈上分配还是在堆上分配。如果是在堆上分配，则进入2.
2. 如果tlab_top + size <= tlab_end，则在在TLAB上直接分配对象并增加tlab_top 的值，如果现有的TLAB不足以存放当前对象则3.
3. 重新申请一个TLAB，并再次尝试存放当前对象。如果放不下，则4。
4. 在Eden区加锁（这个区是多线程共享的），如果eden_top + size <= eden_end则将对象存放在Eden区，增加eden_top 的值，如果Eden区不足以存放，则5。
5. 执行一次Young GC（minor collection）
6. 经过Young GC之后，如果Eden区任然不足以存放当前对象，则直接分配到老年代。

### 对象内存分配的两种方法

1. 指针碰撞(Serial、ParNew等带Compact过程的收集器) ：假设Java堆中内存是绝对规整的，所有用过的内存都放在一边，空闲的内存放在另一边，中间放着一个指针作为分界点的指示器，那所分配内存就仅仅是把那个指针向空闲空间那边挪动一段与对象大小相等的距离，这种分配方式称为“指针碰撞”（Bump the Pointer）。
2. 空闲列表(CMS这种基于Mark-Sweep算法的收集器) ：如果Java堆中的内存并不是规整的，已使用的内存和空闲的内存相互交错，那就没有办法简单地进行指针碰撞了，虚拟机就必须维护一个列表，记录上哪些内存块是可用的，在分配的时候从列表中找到一块足够大的空间划分给对象实例，并更新列表上的记录，这种分配方式称为“空闲列表”（Free List）。

### JVM类加载过程

类从被加载到虚拟机内存中开始，到卸载出内存为止，它的整个生命周期包括：加载、验证、准备、解析、初始化、使用和卸载7个阶段。
1. 加载：通过一个类的全限定名来获取定义此类的二进制字节流，将这个字节流所代表的静态存储结构转化为方法区的运行时数据结构，在内存中生成一个代表这个类的Class对象，作为方法去这个类的各种数据的访问入口
2. 验证：验证是连接阶段的第一步，这一阶段的目的是确保Class文件的字节流中包含的信息符合当前虚拟机的要求，并且不会危害虚拟自身的安全。
3. 准备：准备阶段是正式为类变量分配内存并设置类变量初始值的阶段，这些变量所使用的内存都将在方法去中进行分配。这时候进行内存分配的仅包括类变量（static），而不包括实例变量，实例变量将会在对象实例化时随着对象一起分配在Java堆中。
4. 解析：解析阶段是虚拟机将常量池内的符号（Class文件内的符号）引用替换为直接引用（指针）的过程。
5. 初始化：初始化阶段是类加载过程的最后一步，开始执行类中定义的Java程序代码（字节码）。

### 双亲委派模型

双亲委派的意思是如果一个类加载器需要加载类，那么首先它会把这个类请求委派给父类加载器去完成，每一层都是如此。一直递归到顶层，当父加载器无法完成这个请求时，子类才会尝试去加载。

### 双亲委派模型的"破坏"

一个典型的例子便是JNDI服务，JNDI现在已经是Java的标准服务，它的代码由启动类加载器去加载(在JDK 1.3时放进去的rt.jar)，但JNDI的目的就是对资源进行集中管理和查找，它需要调用由独立厂商实现并部署在应用程序的ClassPath下的JNDI接口提供者(SPI,Service Provider Interface)的代码，但启动类加载器不可能“认识”这些代码那该怎么办?

为了解决这个问题，Java设计团队只好引入了一个不太优雅的设计:线程上下文类加载器(Thread Context ClassLoader)。这个类加载器可以通过java.lang.Thread类的 setContextClassLoaser()方法进行设置，如果创建线程时还未设置，它将会从父线程中继承 一个，如果在应用程序的全局范围内都没有设置过的话，那这个类加载器默认就是应用程序类加载器。

有了线程上下文类加载器，就可以做一些“舞弊”的事情了，JNDI服务使用这个线程上下 文类加载器去加载所需要的SPI代码，也就是父类加载器请求子类加载器去完成类加载的动 作，这种行为实际上就是打通了双亲委派模型的层次结构来逆向使用类加载器，实际上已经 违背了双亲委派模型的一般性原则，但这也是无可奈何的事情。Java中所有涉及SPI的加载动 作基本上都采用这种方式，例如JNDI、JDBC、JCE、JAXB和JBI等。

### JVM锁优化和膨胀过程

1. 自旋锁：自旋锁其实就是在拿锁时发现已经有线程拿了锁，自己如果去拿会阻塞自己，这个时候会选择进行一次忙循环尝试。也就是不停循环看是否能等到上个线程自己释放锁。自适应自旋锁指的是例如第一次设置最多自旋10次，结果在自旋的过程中成功获得了锁，那么下一次就可以设置成最多自旋20次。
2. 锁粗化：虚拟机通过适当扩大加锁的范围以避免频繁的拿锁释放锁的过程。
3. 锁消除：通过逃逸分析发现其实根本就没有别的线程产生竞争的可能（别的线程没有临界量的引用），或者同步块内进行的是原子操作，而“自作多情”地给自己加上了锁。有可能虚拟机会直接去掉这个锁。
4. 偏向锁：在大多数的情况下，锁不仅不存在多线程的竞争，而且总是由同一个线程获得。因此为了让线程获得锁的代价更低引入了偏向锁的概念。偏向锁的意思是如果一个线程获得了一个偏向锁，如果在接下来的一段时间中没有其他线程来竞争锁，那么持有偏向锁的线程再次进入或者退出同一个同步代码块，不需要再次进行抢占锁和释放锁的操作。
5. 轻量级锁：当存在超过一个线程在竞争同一个同步代码块时，会发生偏向锁的撤销。当前线程会尝试使用CAS来获取锁，当自旋超过指定次数(可以自定义)时仍然无法获得锁，此时锁会膨胀升级为重量级锁。
6. 重量级锁：重量级锁依赖对象内部的monitor锁来实现，而monitor又依赖操作系统的MutexLock（互斥锁）。当系统检查到是重量级锁之后，会把等待想要获取锁的线程阻塞，被阻塞的线程不会消耗CPU，但是阻塞或者唤醒一个线程，都需要通过操作系统来实现。

### 什么情况下需要开始类加载过程的第一个阶段加载

1. 遇到new、getstatic、putstatic或invokestatic这4条字节码指令时，如果类没有进行过初始化，则需要先触发其初始化。生成这4条指令的最常见的Java代码场景是：使用new关键字实例化对象的时候、读取或设置一个类的静态字段（被final修饰、已在编译期把结果放入常量池的静态字段除外）的时候，以及调用一个类的静态方法的时候。
2. 使用java.lang.reflect包的方法对类进行反射调用的时候，如果类没有进行过初始化，则需要先触发其初始化。
3. 当初始化一个类的时候，如果发现其父类还没有进行过初始化，则需要先触发其父类的初始化。
4. 当虚拟机启动时，用户需要指定一个要执行的主类（包含main（）方法的那个类），虚拟机会先初始化这个主类。

### i++操作的字节码指令

1. 将int类型常量加载到操作数栈顶
2. 将int类型数值从操作数栈顶取出，并存储到到局部变量表的第1个Slot中
3. 将int类型变量从局部变量表的第1个Slot中取出，并放到操作数栈顶
4. 将局部变量表的第1个Slot中的int类型变量加1
5. 表示将int类型数值从操作数栈顶取出，并存储到到局部变量表的第1个Slot中，即i中

### JVM性能监控

1. JDK的命令行工具

    * jps(虚拟机进程状况工具)：jps可以列出正在运行的虚拟机进程，并显示虚拟机执行主类(Main Class,main()函数所在的类)名称 以及这些进程的本地虚拟机唯一ID(Local Virtual Machine Identifier,LVMID)。
    * jstat(虚拟机统计信息监视工具)：jstat是用于监视虚拟机各种运行状态信息的命令行工 具。它可以显示本地或者远程虚拟机进程中的类装载、内存、垃圾收集、JIT编译等运行数据。
    * jinfo(Java配置信息工具)：jinfo的作用是实时地查看和调整虚拟机各项参数。
    * jmap(Java内存映像工具)：命令用于生成堆转储快照(一般称为heapdump或dump文 件)。如果不使用jmap命令，要想获取Java堆转储快照，还有一些比较“暴力”的手段:譬如 在第2章中用过的-XX:+HeapDumpOnOutOfMemoryError参数，可以让虚拟机在OOM异常出 现之后自动生成dump文件。jmap的作用并不仅仅是为了获取dump文件，它还可以查询finalize执行队列、Java堆和永 久代的详细信息，如空间使用率、当前用的是哪种收集器等。
    * jhat(虚拟机堆转储快照分析工具)：jhat命令与jmap搭配使用，来分析jmap生成的堆 转储快照。jhat内置了一个微型的HTTP/HTML服务器，生成dump文件的分析结果后，可以在 浏览器中查看。
    * jstack(Java堆栈跟踪工具)：jstack命令用于生成虚拟机当前时刻的线程快照。线程快照就是当前虚拟机内每一条线程正在执行的方法堆栈 的集合，生成线程快照的主要目的是定位线程出现长时间停顿的原因，如线程间死锁、死循 环、请求外部资源导致的长时间等待等都是导致线程长时间停顿的常见原因。线程出现停顿 的时候通过jstack来查看各个线程的调用堆栈，就可以知道没有响应的线程到底在后台做些 什么事情，或者等待着什么资源。

2. JDK的可视化工具

    * JConsole
    * VisualVM

### JVM常见参数

1. -Xms20M：表示设置JVM启动内存的最小值为20M，必须以M为单位
2. -Xmx20M：表示设置JVM启动内存的最大值为20M，必须以M为单位。将-Xmx和-Xms设置为一样可以避免JVM内存自动扩展。大的项目-Xmx和-Xms一般都要设置到10G、20G甚至还要高
3. -verbose:gc：表示输出虚拟机中GC的详细情况
4. -Xss128k：表示可以设置虚拟机栈的大小为128k
5. -Xoss128k：表示设置本地方法栈的大小为128k。不过HotSpot并不区分虚拟机栈和本地方法栈，因此对于HotSpot来说这个参数是无效的
6. -XX:PermSize=10M：表示JVM初始分配的永久代（方法区）的容量，必须以M为单位
7. -XX:MaxPermSize=10M：表示JVM允许分配的永久代（方法区）的最大容量，必须以M为单位，大部分情况下这个参数默认为64M
8. -Xnoclassgc：表示关闭JVM对类的垃圾回收
9. -XX:+TraceClassLoading表示查看类的加载信息
10. -XX:+TraceClassUnLoading：表示查看类的卸载信息
11. -XX:NewRatio=4：表示设置年轻代（包括Eden和两个Survivor区）/老年代 的大小比值为1：4，这意味着年轻代占整个堆的1/5
12. -XX:SurvivorRatio=8：表示设置2个Survivor区：1个Eden区的大小比值为2:8，这意味着Survivor区占整个年轻代的1/5，这个参数默认为8
13. -Xmn20M：表示设置年轻代的大小为20M
14. -XX:+HeapDumpOnOutOfMemoryError：表示可以让虚拟机在出现内存溢出异常时Dump出当前的堆内存转储快照
15. -XX:+UseG1GC：表示让JVM使用G1垃圾收集器
16. -XX:+PrintGCDetails：表示在控制台上打印出GC具体细节
17. -XX:+PrintGC：表示在控制台上打印出GC信息
18. -XX:PretenureSizeThreshold=3145728：表示对象大于3145728（3M）时直接进入老年代分配，这里只能以字节作为单位
19. -XX:MaxTenuringThreshold=1：表示对象年龄大于1，自动进入老年代,如果设置为0的话，则年轻代对象不经过Survivor区，直接进入年老代。对于年老代比较多的应用，可以提高效率。如果将此值设置为一个较大值，则年轻代对象会在Survivor区进行多次复制，这样可以增加对象在年轻代的存活时间，增加在年轻代被回收的概率。
20. -XX:CompileThreshold=1000：表示一个方法被调用1000次之后，会被认为是热点代码，并触发即时编译
21. -XX:+PrintHeapAtGC：表示可以看到每次GC前后堆内存布局
22. -XX:+PrintTLAB：表示可以看到TLAB的使用情况
23. -XX:+UseSpining：开启自旋锁
24. -XX:PreBlockSpin：更改自旋锁的自旋次数，使用这个参数必须先开启自旋锁
25. -XX:+UseSerialGC：表示使用jvm的串行垃圾回收机制，该机制适用于单核cpu的环境下
26. -XX:+UseParallelGC：表示使用jvm的并行垃圾回收机制，该机制适合用于多cpu机制，同时对响应时间无强硬要求的环境下，使用-XX:ParallelGCThreads=<N>设置并行垃圾回收的线程数，此值可以设置与机器处理器数量相等。
27. -XX:+UseParallelOldGC：表示年老代使用并行的垃圾回收机制
28. -XX:+UseConcMarkSweepGC：表示使用并发模式的垃圾回收机制，该模式适用于对响应时间要求高，具有多cpu的环境下
29. -XX:MaxGCPauseMillis=100：设置每次年轻代垃圾回收的最长时间，如果无法满足此时间，JVM会自动调整年轻代大小，以满足此值。
30. -XX:+UseAdaptiveSizePolicy：设置此选项后，并行收集器会自动选择年轻代区大小和相应的Survivor区比例，以达到目标系统规定的最低响应时间或者收集频率等，此值建议使用并行收集器时，一直打开

### JVM调优目标-何时需要做jvm调优

1. heap 内存（老年代）持续上涨达到设置的最大内存值；
2. Full GC 次数频繁；
3. GC 停顿时间过长（超过1秒）；
4. 应用出现OutOfMemory 等内存异常；
5. 应用中有使用本地缓存且占用大量内存空间；
6. 系统吞吐量与响应性能不高或下降。

### JVM调优实战

1. Major GC和Minor GC频繁

   首先优化Minor GC频繁问题。通常情况下，由于新生代空间较小，Eden区很快被填满，就会导致频繁Minor GC，因此可以通过增大新生代空间来降低Minor GC的频率。例如在相同的内存分配率的前提下，新生代中的Eden区增加一倍，Minor GC的次数就会减少一半。

   扩容Eden区虽然可以减少Minor GC的次数，但会增加单次Minor GC时间么？扩容后，Minor GC时增加了T1（扫描时间），但省去T2（复制对象）的时间，更重要的是对于虚拟机来说，复制对象的成本要远高于扫描成本，所以，单次Minor GC时间更多取决于GC后存活对象的数量，而非Eden区的大小。因此如果堆中短期对象很多，那么扩容新生代，单次Minor GC时间不会显著增加。

2. 请求高峰期发生GC，导致服务可用性下降

   由于跨代引用的存在，CMS在Remark阶段必须扫描整个堆，同时为了避免扫描时新生代有很多对象，增加了可中断的预清理阶段用来等待Minor GC的发生。只是该阶段有时间限制，如果超时等不到Minor GC，Remark时新生代仍然有很多对象，我们的调优策略是，通过参数强制Remark前进行一次Minor GC，从而降低Remark阶段的时间。
   另外，类似的JVM是如何避免Minor GC时扫描全堆的？ 经过统计信息显示，老年代持有新生代对象引用的情况不足1%，根据这一特性JVM引入了卡表（card table）来实现这一目的。卡表的具体策略是将老年代的空间分成大小为512B的若干张卡（card）。卡表本身是单字节数组，数组中的每个元素对应着一张卡，当发生老年代引用新生代时，虚拟机将该卡对应的卡表元素设置为适当的值。如上图所示，卡表3被标记为脏（卡表还有另外的作用，标识并发标记阶段哪些块被修改过），之后Minor GC时通过扫描卡表就可以很快的识别哪些卡中存在老年代指向新生代的引用。这样虚拟机通过空间换时间的方式，避免了全堆扫描。

3. STW过长的GC

   对于性能要求很高的服务，建议将MaxPermSize和MinPermSize设置成一致（JDK8开始，Perm区完全消失，转而使用元空间。而元空间是直接存在内存中，不在JVM中），Xms和Xmx也设置为相同，这样可以减少内存自动扩容和收缩带来的性能损失。虚拟机启动的时候就会把参数中所设定的内存全部化为私有，即使扩容前有一部分内存不会被用户代码用到，这部分内存在虚拟机中被标识为虚拟内存，也不会交给其他进程使用。

4. 外部命令导致系统缓慢

   一个数字校园应用系统，发现请求响应时间比较慢，通过操作系统的mpstat工具发现CPU使用率很高，并且系统占用绝大多数的CPU资 源的程序并不是应用系统本身。每个用户请求的处理都需要执行一个外部shell脚本来获得系统的一些信息，执行这个shell脚本是通过Java的 Runtime.getRuntime().exec()方法来调用的。这种调用方式可以达到目的，但是它在Java 虚拟机中是非常消耗资源的操作，即使外部命令本身能很快执行完毕，频繁调用时创建进程 的开销也非常可观。Java虚拟机执行这个命令的过程是:首先克隆一个和当前虚拟机拥有一样环境变量的进程，再用这个新的进程去执行外部命令，最后再退出这个进程。如果频繁执行这个操作，系统的消耗会很大，不仅是CPU，内存负担也很重。用户根据建议去掉这个Shell脚本执行的语句，改为使用Java的API去获取这些信息后，系统很快恢复了正常。

5. 由Windows虚拟内存导致的长时间停顿
 q
   一个带心跳检测功能的GUI桌面程序，每15秒会发送一次心跳检测信号，如果对方30秒以内都没有信号返回，那就认为和对方程序的连接已经断开。程序上线后发现心跳 检测有误报的概率，查询日志发现误报的原因是程序会偶尔出现间隔约一分钟左右的时间完 全无日志输出，处于停顿状态。

   因为是桌面程序，所需的内存并不大(-Xmx256m)，所以开始并没有想到是GC导致的 程序停顿，但是加入参数-XX:+PrintGCApplicationStoppedTime-XX:+PrintGCDateStamps- Xloggc:gclog.log后，从GC日志文件中确认了停顿确实是由GC导致的，大部分GC时间都控 制在100毫秒以内，但偶尔就会出现一次接近1分钟的GC。

   从GC日志中找到长时间停顿的具体日志信息(添加了-XX:+PrintReferenceGC参数)， 找到的日志片段如下所示。从日志中可以看出，真正执行GC动作的时间不是很长，但从准 备开始GC，到真正开始GC之间所消耗的时间却占了绝大部分。

   除GC日志之外，还观察到这个GUI程序内存变化的一个特点，当它最小化的时候，资源 管理中显示的占用内存大幅度减小，但是虚拟内存则没有变化，因此怀疑程序在最小化时它的工作内存被自动交换到磁盘的页面文件之中了，这样发生GC时就有可能因为恢复页面文件的操作而导致不正常的GC停顿。在Java的GUI程序中要避免这种现象，可以 加入参数“-Dsun.awt.keepWorkingSetOnMinimize=true”来解决。
