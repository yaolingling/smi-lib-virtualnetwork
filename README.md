lib-virtualnetwork git repository

[![Quality Gate](http://100.68.126.201:9000/api/badges/gate?key=com.dell.isg.smi:virtualnetwork)](http://100.68.126.201:9000/dashboard/index/com.dell.isg.smi:virtualnetwork)

### Overview
The virtualnetwork project builds a Java JAR library that makes avalable an API to consuming java programs.  It encapsulates business logic for crud of network definitions/settings, static IP address pools, and an IP Address Management (IPAM) reservation process.

### How to use
The library is published to the following GTIE artifactory server: https://gtie-artifactory.us.dell.com

##### Maven Example:
~~~
<dependency>
    <groupId>com.dell.isg.smi</groupId>
    <artifactId>virtualnetwork</artifactId>
    <version>1.0.75</version>
</dependency>
~~~

##### Gradle Example:
~~~
compile(group: 'com.dell.isg.smi', name: 'virtualnetwork', version: '1.0.75')
~~~

### Support
Support can be obtained from the slack channel:
codecommunity.slack.com