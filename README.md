amber-java-joystick
===================

Requirements
------------

* `jre`
* `amber` mediator with `roboclaw` driver
* `joystick`

Tested on *Windows XP*.

How to use (java)
-----------------

 * Download `*.jar` from this repository, from `target` (direct [link](https://github.com/dev-amber/amber-java-joystick/raw/master/target/amber-java-joystick-1.0-SNAPSHOT-jar-with-dependencies.jar "Latest build"))
 * Run it using `java` with arguments `<amber_instance_ip_address:string> <joystick_id:decimal> <max_speed:decimal>
   * `amber_instance_ip_address` points to IP of machine/robot where Amber is running with Roboclaw driver
   * `joystick_id` points to detected joystick ID inside your system (it must be decimal)
   * `max_speed` determine maximum speed expressed in units of `mm/s`

Example:

    java -jar /path/to/amber-java-joystick-1.0-SNAPSHOT-jar-with-dependencies.jar 192.168.0.100 1 700
