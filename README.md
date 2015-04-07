# News #

  * Code Complete : [Snapshot](https://oss.sonatype.org/index.html#nexus-search;gav~org.ivcode~guice-asynchronous~~~)
<br />
<br />

# About #

The goal of this project is to extend Guice's AOP functionality and support asynchronous method calls. To asynchronize a method, users simply mark methods with the _@Asynchronous_ annotation. With this, we abstract away the cross-cutting concern of managing asynchronous calls and reduce the amount of code bloat that goes along with making asynchronous calls.

### Problem ###
Guice is a simple dependency injection framework by Google and is well received and widely used. Unfortunately, along with its simplicity comes some limitations. Guice's AOP allows users to implement stacking around action method interceptors. In this, a set of interceptors will wrap a method call such that one interceptor calls the next (down the stack) until the method is finally called. Afterward, the interceptors continue their execution returning to the interceptor before it (back up the stack).

The problem is that, from within an interceptor, there's no clean way to offload calls to other threads. Offloading the task and returning to the caller causes the stack to jump out of its assumed order. In fact, when the next interceptor is called from another thread, the wrong interceptor executes.

### Solution ###
Our solution is to wrap Guice's AOP functionality with our own proxy and offload Guice's entire interception stack to another thread. This allows us to support both asynchronous method calls and any interceptors the method may have.

<br />
<br />

# Limitations #

  * Asynchronous methods must return void (See Callbacks & Futures)
  * The just-in-time binder dose not asynchronise methods. Classes must be explicitly bound.
<font size='1'>See <a href='https://github.com/google/guice/wiki/AOP#limitations'>Guice's AOP Limitations</a> for general limitations regarding AOP.</font>
