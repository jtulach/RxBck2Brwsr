package org.apidesign.rxjava.sample;

import java.util.List;
import net.java.html.json.ComputedProperty;
import net.java.html.json.Function;
import net.java.html.json.Model;
import net.java.html.json.Property;
import rx.Observable;
import rx.observables.StringObservable;

/** Model annotation generates class Data with 
 * one message property, boolean property and read only words property
 */
@Model(className = "Data", targetId="", properties = {
    @Property(name = "message", type = String.class),
    @Property(name = "rotating", type = boolean.class)
})
final class DataModel {
    @ComputedProperty static java.util.List<String> words(String message) {
        Observable<List<String>> observable = StringObservable.split(Observable.just(message), " ").
            mergeWith(Observable.just("!").repeat()).
            take(6).toList();
        return observable.toBlocking().first();
    }
    
    @Function static void turnAnimationOn(Data model) {
        model.setRotating(true);
    }
    
    @Function static void turnAnimationOff(final Data model) {
        confirmByUser("Really turn off?", new Runnable() {
            @Override
            public void run() {
                model.setRotating(false);
            }
        });
    }
    
    @Function static void rotate5s(final Data model) {
        model.setRotating(true);
        java.util.Timer timer = new java.util.Timer("Rotates a while");
        timer.schedule(new java.util.TimerTask() {
            @Override
            public void run() {
                model.setRotating(false);
            }
        }, 5000);
    }
    
    @Function static void showScreenSize(Data model) {
        model.setMessage(screenSize());
    }
    
    /** Shows direct interaction with JavaScript */
    @net.java.html.js.JavaScriptBody(
        args = { "msg", "callback" }, 
        javacall = true, 
        body = "if (confirm(msg)) {\n"
             + "  callback.@java.lang.Runnable::run()();\n"
             + "}\n"
    )
    static native void confirmByUser(String msg, Runnable callback);
    @net.java.html.js.JavaScriptBody(
        args = {}, body = 
        "var w = window,\n" +
        "    d = document,\n" +
        "    e = d.documentElement,\n" +
        "    g = d.getElementsByTagName('body')[0],\n" +
        "    x = w.innerWidth || e.clientWidth || g.clientWidth,\n" +
        "    y = w.innerHeight|| e.clientHeight|| g.clientHeight;\n" +
        "\n" +
        "return 'Screen size is ' + x + ' times ' + y;\n"
    )
    static native String screenSize();
}
