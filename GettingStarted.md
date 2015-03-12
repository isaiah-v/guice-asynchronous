## About ##

---

guice-asynchronous extends guice’s AOP functionality to support asynchronous method calls. To asynchronize a method, users simply mark methods with the _@Asynchronous_ annotation. With this, we abstract away the cross-cutting concern of managing asynchronous calls and reduce the amount of code bloat that goes along with making asynchronous calls.
<br />
## Setup ##

---

If you are familiar with guice, setting up guice-asynchronous is straightforward and typical to any 3rd-party guice library. The process is to Markup, Bind, and Execute.

### 1) Markup ###
The first step in asynchronizing your methods is marking your methods as being asynchronous with the _@Asynchronous_ annotation.

```
import org.ivcode.guice.asynchronous.Asynchronous;

/** Asynchronous Class */
public class MyAsynchronousClass {
        @Asynchronous
        public void sayHello() {
                System.out.println("Hello from thread " + Thread.currentThread().getName());
        }
}
```

Because an asynchronous methods don't run on the same thread as it's caller, the return type must be void. The only way to return value is through a callback. For information on using callbacks and futures, see the [Helpers](Helpers.md) wiki.

### 2) Bind ###
Binding asynchronous classes is done using an _AsynchronousBinder_. This binder is accessible through the _AsynchronousModule_.

```
import org.ivcode.guice.asynchronous.AsynchronousBinder;
import org.ivcode.guice.asynchronous.AsynchronousModule;

/** Asynchronous Module */
public class MyAsynchronousModule implements AsynchronousModule {
        public void configure(AsynchronousBinder binder) {
                // bind my asynchronous class
                binder.bindAsynchronous(MyAsynchronousClass.class);
        }
}
```

### 3) Execute ###
In guice-asynchronous, tasks are handled by the an instance of _GuiceAsynchronous_. By default, this will run tasks on daemon threads, meaning that your application die when the main thread dies. In a normal workflow, you will kickoff a task (who will also kick off subsequent tasks), then you will join on the context and exit the main thread cleanly.

```
import org.ivcode.guice.asynchronous.GuiceAsynchronous;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class Main {
        
        /** Main */
        public static void main(String... strings) throws InterruptedException {
                // create guice-asynchronous instance
                GuiceAsynchronous guiceAsync = new GuiceAsynchronous();
                
                // create injector
                Injector i = Guice.createInjector(guiceAsync.createModule(new MyAsynchronousModule ()));
                
                // poll instance
                MyAsynchronousClass myClass = i.getInstance(MyAsynchronousClass.class);
                
                // kickoff tasks
                myClass.sayHello();
                
                // join on tasks and shutdown
                guiceAsync.shutdown();
        }
}
```

The shutdown() method causes the current thread to wait for all asynchronous tasks to complete. Tasks can still be submitted, allowing a task to call asynchronous methods, but as soon as there are zero running tasks, subsequent submissions will fail.  To forcefully shutdown the context, use shutdownNow().

<br />
## Guice AOP ##

---

While the guice-asynchronous project implements its own cglib proxy, guice’s existing APO functionality is fully supported. However, guice-asynchronous only has visibility to method interceptors bound by the AsynchronousBinder.  To make sure all method interceptors are visible, install modules using the AsynchronousBinder.

```
// TODO install example
```

<br />
## Examples ##

---

**Basic Asynchronous Binding**
```
import org.ivcode.guice.asynchronous.Asynchronous;
import org.ivcode.guice.asynchronous.AsynchronousBinder;
import org.ivcode.guice.asynchronous.AsynchronousModule;
import org.ivcode.guice.asynchronous.GuiceAsynchronous;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class Main {
        
        /** Main */
        public static void main(String... strings) throws InterruptedException {
                // create guice-asynchronous instance
                GuiceAsynchronous guiceAsync = new GuiceAsynchronous();
                
                // create injector
                Injector i = Guice.createInjector(guiceAsync.createModule(new MyAsynchronousModule ()));
                
                // poll instance
                MyAsynchronousClass myClass = i.getInstance(MyAsynchronousClass.class);
                
                // kickoff tasks
                myClass.sayHello();
                
                // join on tasks and shutdown
                guiceAsync.shutdown();
        }
        
        /** Asynchronous Module */
        public static class MyAsynchronousModule implements AsynchronousModule {
                public void configure(AsynchronousBinder binder) {
                        // bind my asynchronous class
                        binder.bindAsynchronous(MyAsynchronousClass.class);
                }
        }
        
        /** Asynchronous Class */
        public static class MyAsynchronousClass {
                @Asynchronous
                public void sayHello() {
                        System.out.println("Hello from thread " + Thread.currentThread().getName());
                }
        }
}
```