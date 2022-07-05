# What
**Loghub** is a log delegate.

# Quick Start
select log implement:
```xml
		<dependency>
			<groupId>com.dinstone.loghub</groupId>
			<artifactId>loghub-core</artifactId>
			<version>1.3.1</version>
		</dependency>
```
or
```xml
		<dependency>
			<groupId>com.dinstone.loghub</groupId>
			<artifactId>loghub-slf4j</artifactId>
			<version>1.3.1</version>
		</dependency>
```

# Example
##JUL##

```java
	@Test
	public void test01() throws InterruptedException {
		LoggerFactory.getLogger("");

		JulOption option = new JulOption().setPattern("logs/loghub.log").setLimitDays(3);
		JulDelegateFactory factory = new JulDelegateFactory(option);
		LoggerFactory.initialise(factory);

		Logger logger = LoggerFactory.getLogger(LoggerTest.class);
		for (int i = 0; i < 5; i++) {
			logger.info("case 01 {},{}", i);
		}

		Thread.sleep(1000);
	}
```

##SLF4j/Log4j/Log4j2##
```java
	@Test
	public void test00() throws InterruptedException {
		Logger logger = LoggerFactory.getLogger(LoggerTest.class);
		for (int i = 0; i < 5; i++) {
			logger.info("case 00 {},{}", i);
		}

		Thread.sleep(1000);
	}
```
