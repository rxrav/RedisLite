package com.github.rxrav.redislite.core.cmd;

import com.github.rxrav.redislite.AppTest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.CommandArguments;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Protocol;
import redis.clients.jedis.commands.ProtocolCommand;
import redis.clients.jedis.exceptions.JedisDataException;
import redis.clients.jedis.params.SetParams;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class JedisClientTest {

    Logger logger = LogManager.getLogger(JedisClientTest.class);

    @BeforeAll
    static void _startServer() {
        AppTest.startServer();
    }

    @AfterAll
    static void _stopServer() throws IOException {
        AppTest.stopServer();
    }

    @Test
    void shouldPing() {
        try(Jedis jedis = new Jedis("127.0.0.1", 6379);) {
            String actual = jedis.ping();
            logger.info(STR."Ping test, received: \{actual}");
            assertEquals("PONG", actual);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw e;
        }
    }

    @Test
    void shouldEcho() {
        try(Jedis jedis = new Jedis("127.0.0.1", 6379);) {
            String actual = jedis.echo("hello world");
            logger.info(STR."Echo test, received: \{actual}");
            assertEquals("hello world", actual);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw e;
        }
    }

    @Test
    void shouldSet() {
        try(Jedis jedis = new Jedis("127.0.0.1", 6379);) {
            String actual = jedis.set("name", "john");
            logger.info(STR."Set test, received: \{actual}");
            assertEquals("OK", actual);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw e;
        }
    }

    @Test
    void shouldSetWithExThenWaitThenGetNull() throws InterruptedException {
        try(Jedis jedis = new Jedis("127.0.0.1", 6379);) {
            SetParams params = new SetParams();
            params.ex(4);
            String actual = jedis.set("name-ex", "jim", params);
            logger.info(STR."Set with ex test, received: \{actual}");
            assertEquals("OK", actual);

            actual = jedis.get("name-ex");
            logger.info(STR."Get test, received: \{actual}");
            assertEquals("jim", actual);

            logger.info("Will wait 5 seconds to expire...");
            Thread.sleep(5 * 1000);

            actual = jedis.get("name-ex");
            logger.info(STR."Get test (again) to check expired, received: \{actual}");
            assertNull(actual);

        } catch (Exception e) {
            logger.error(e.getMessage());
            throw e;
        }
    }

    @Test
    void shouldNotSetWithExNegativeTime() throws InterruptedException {
        try(Jedis jedis = new Jedis("127.0.0.1", 6379);) {
            SetParams params = new SetParams();
            params.ex(-4);
            JedisDataException ex = assertThrows(JedisDataException.class, () -> {
                jedis.set("name-ex", "jim", params);
            });
            assertEquals("VALERR Time is in past, can't use with EX", ex.getMessage());

        } catch (Exception e) {
            logger.error(e.getMessage());
            throw e;
        }
    }

    @Test
    void shouldNotSetWithPxNegativeTime() throws InterruptedException {
        try(Jedis jedis = new Jedis("127.0.0.1", 6379);) {
            SetParams params = new SetParams();
            params.px(-4);
            JedisDataException ex = assertThrows(JedisDataException.class, () -> {
                jedis.set("name-px", "jim", params);
            });
            assertEquals("VALERR Time is in past, can't use with PX", ex.getMessage());

        } catch (Exception e) {
            logger.error(e.getMessage());
            throw e;
        }
    }

    @Test
    void shouldSetWithPxThenWaitThenGetNull() throws InterruptedException {
        try(Jedis jedis = new Jedis("127.0.0.1", 6379);) {
            SetParams params = new SetParams();
            params.px(500);
            String actual = jedis.set("name-px", "jimmy", params);
            logger.info(STR."Set with px test, received: \{actual}");
            assertEquals("OK", actual);

            actual = jedis.get("name-px");
            logger.info(STR."Get test, received: \{actual}");
            assertEquals("jimmy", actual);

            logger.info("Will wait 1 second to expire...");
            Thread.sleep(1000);

            actual = jedis.get("name-px");
            logger.info(STR."Get test (again) to check expired, received: \{actual}");
            assertNull(actual);

        } catch (Exception e) {
            logger.error(e.getMessage());
            throw e;
        }
    }

    @Test
    void shouldGet() {
        try(Jedis jedis = new Jedis("127.0.0.1", 6379);) {
            jedis.set("name", "jane");
            String actual = jedis.get("name");
            logger.info(STR."Get test, received: \{actual}");
            assertEquals("jane", actual);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw e;
        }
    }

    @Test
    void shouldGetNullValForNonExistentKey() {
        try(Jedis jedis = new Jedis("127.0.0.1", 6379);) {
            String actual = jedis.get("name-X");
            logger.info(STR."Get test, received: \{actual}");
            assertNull(actual);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw e;
        }
    }

    @Test
    void shouldSetWithNxWhenKeyIsNotThere() {
        try(Jedis jedis = new Jedis("127.0.0.1", 6379);) {
            SetParams params = new SetParams();
            params.nx();
            String actual = jedis.set("newkeynx", "john", params);
            logger.info(STR."Set test, received: \{actual}");
            assertEquals("OK", actual);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw e;
        }
    }

    @Test
    void shouldNotSetWithNxWhenKeyIsThere() {
        try(Jedis jedis = new Jedis("127.0.0.1", 6379);) {
            jedis.set("newkey-nx", "john");
            SetParams params = new SetParams();
            params.nx();
            String actual = jedis.set("newkey-nx", "john doe", params);
            logger.info(STR."Set test, received: \{actual}");
            assertNull(actual);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw e;
        }
    }

    @Test
    void shouldSetWithXxWhenKeyIsThere() {
        try(Jedis jedis = new Jedis("127.0.0.1", 6379);) {
            jedis.set("newkey-xx", "john");
            SetParams params = new SetParams();
            params.xx();
            String actual = jedis.set("newkey-xx", "john doe", params);
            logger.info(STR."Set test, received: \{actual}");
            assertEquals("OK", actual);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw e;
        }
    }

    @Test
    void shouldNotSetWithXxWhenKeyIsNotThere() {
        try(Jedis jedis = new Jedis("127.0.0.1", 6379);) {
            SetParams params = new SetParams();
            params.xx();
            String actual = jedis.set("newkey-xxnot", "john doe", params);
            logger.info(STR."Set test, received: \{actual}");
            assertNull(actual);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw e;
        }
    }

    @Test
    void shouldThrowErrorForSetWithBadOpt() {
        String badOpt = "qx";
        try(Jedis jedis = new Jedis("127.0.0.1", 6379);) {
            JedisDataException ex = assertThrows(JedisDataException.class, () -> {
                jedis.sendCommand(
                        Protocol.Command.valueOf("SET"),
                        "x", "10", badOpt);
            });
            assertEquals("VALERR Hmm.. seems like a syntax error", ex.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw e;
        }
    }

    @Test
    void shouldThrowErrorForSetForBadOptAfterNx() {
        String badOpt = "qx";
        try(Jedis jedis = new Jedis("127.0.0.1", 6379);) {
            JedisDataException ex = assertThrows(JedisDataException.class, () -> {
                jedis.sendCommand(
                        Protocol.Command.valueOf("SET"),
                        "x", "10", "nx", badOpt);
            });
            assertEquals("VALERR Hmm.. seems like a syntax error", ex.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw e;
        }
    }

    @Test
    void shouldThrowErrorForSetForBadExpTime1() {
        String badOpt = "I00";
        try(Jedis jedis = new Jedis("127.0.0.1", 6379);) {
            JedisDataException ex = assertThrows(JedisDataException.class, () -> {
                jedis.sendCommand(
                        Protocol.Command.valueOf("SET"),
                        "x", "10", "nx", "ex", badOpt);
            });
            assertEquals("VALERR Time value is not a number", ex.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw e;
        }
    }

    @Test
    void shouldThrowErrorForSetForBadExpTime2() {
        String badOpt = "I00";
        try(Jedis jedis = new Jedis("127.0.0.1", 6379);) {
            JedisDataException ex = assertThrows(JedisDataException.class, () -> {
                jedis.sendCommand(
                        Protocol.Command.valueOf("SET"),
                        "x", "10", "ex", badOpt);
            });
            assertEquals("VALERR Time value is not a number", ex.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw e;
        }
    }

    @Test
    void shouldThrowErrorForSetForBadExpTime3() {
        String misplacedNumberValue = "100";
        try(Jedis jedis = new Jedis("127.0.0.1", 6379);) {
            JedisDataException ex = assertThrows(JedisDataException.class, () -> {
                jedis.sendCommand(
                        Protocol.Command.valueOf("SET"),
                        "x", "10", "xx", misplacedNumberValue);
            });
            assertEquals("VALERR Misplaced number value", ex.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw e;
        }
    }

    @Test
    void shouldThrowErrorForSetSyntaxErr() {
        try(Jedis jedis = new Jedis("127.0.0.1", 6379);) {
            JedisDataException ex = assertThrows(JedisDataException.class, () -> {
                jedis.sendCommand(
                        Protocol.Command.valueOf("SET"),
                        "x", "10", "xx", "px");
            });
            assertEquals("VALERR Hmm.. seems like a syntax error", ex.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw e;
        }
    }

    @Test
    void shouldCountExistingKeys() {
        try(Jedis jedis = new Jedis("127.0.0.1", 6379);) {
            jedis.set("name1", "john");
            jedis.set("name2", "jim");
            long k = jedis.exists("name1", "name2");
            assertEquals(2, k);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw e;
        }
    }

    @Test
    void shouldCountExistingKeysMultipleTimes() {
        try(Jedis jedis = new Jedis("127.0.0.1", 6379);) {
            jedis.set("name1", "john");
            jedis.set("name2", "jim");
            long k = jedis.exists("name1", "name1", "name2", "name10");
            assertEquals(3, k);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw e;
        }
    }

    @Test
    void shouldCountNonExistentKeysAsZero() {
        try(Jedis jedis = new Jedis("127.0.0.1", 6379);) {
            jedis.set("name1", "john");
            boolean k = jedis.exists("name5");
            assertFalse(k);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw e;
        }
    }

    @Test
    void shouldDeleteOneKey() {
        try(Jedis jedis = new Jedis("127.0.0.1", 6379);) {
            jedis.set("name-del", "john");
            long k = jedis.del("name-del");
            assertEquals(1, k);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw e;
        }
    }

    @Test
    void shouldDeleteMultipleKeys() {
        try(Jedis jedis = new Jedis("127.0.0.1", 6379);) {
            jedis.set("name-del1", "john");
            jedis.set("name-del2", "jim");
            jedis.set("name-del3", "jane");
            long k = jedis.del("name-del1", "name-del2", "name-del3");
            assertEquals(3, k);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw e;
        }
    }

    @Test
    void shouldIncrByOne() {
        try(Jedis jedis = new Jedis("127.0.0.1", 6379);) {
            jedis.set("incr1", "1");
            long k = jedis.incr("incr1");
            assertEquals(2, k);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw e;
        }
    }

    @Test
    void shouldSetToZeroAndIncrByOne() {
        try(Jedis jedis = new Jedis("127.0.0.1", 6379);) {
            long k = jedis.incr("incr-none");
            assertEquals(1, k);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw e;
        }
    }

    @Test
    void shouldThrowErrForIncr() {
        try(Jedis jedis = new Jedis("127.0.0.1", 6379);) {
            jedis.set("incr-ex", "1b");
            JedisDataException ex = assertThrows(JedisDataException.class, () -> {
                jedis.incr("incr-ex");
            });
            assertEquals("WRONGTYPE Not a valid number type", ex.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw e;
        }
    }

    @Test
    void shouldDecrByOne() {
        try(Jedis jedis = new Jedis("127.0.0.1", 6379);) {
            jedis.set("decr1", "2");
            long k = jedis.decr("decr1");
            assertEquals(1, k);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw e;
        }
    }

    @Test
    void shouldSetToZeroAndDecrByOne() {
        try(Jedis jedis = new Jedis("127.0.0.1", 6379);) {
            long k = jedis.decr("decr-none");
            assertEquals(-1, k);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw e;
        }
    }

    @Test
    void shouldThrowErrForDecr() {
        try(Jedis jedis = new Jedis("127.0.0.1", 6379);) {
            jedis.set("decr-ex", "1b");
            JedisDataException ex = assertThrows(JedisDataException.class, () -> {
                jedis.decr("decr-ex");
            });
            assertEquals("WRONGTYPE Not a valid number type", ex.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw e;
        }
    }

    @Test
    void shouldLpush() {
        try(Jedis jedis = new Jedis("127.0.0.1", 6379);) {
            long k = jedis.lpush("lpushok", "1", "2", "3");
            assertEquals(3, k);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw e;
        }
    }

    @Test
    void shouldLpushAndLpushAgain() {
        try(Jedis jedis = new Jedis("127.0.0.1", 6379);) {
            jedis.lpush("lpushok1", "1", "2", "3");
            long k = jedis.lpush("lpushok1", "4", "5");
            assertEquals(5, k);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw e;
        }
    }

    @Test
    void shouldLrangeInCorrectOrderAfterMultipleLpush() {
        try(Jedis jedis = new Jedis("127.0.0.1", 6379);) {
            jedis.lpush("lpushok2", "1", "2", "3");
            jedis.lpush("lpushok2", "4", "5");
            List<String> out = jedis.lrange("lpushok2", 0, 10);
            assertEquals(List.of("5", "4", "3", "2", "1"), out);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw e;
        }
    }

    @Test
    void shouldRpush() {
        try(Jedis jedis = new Jedis("127.0.0.1", 6379);) {
            long k = jedis.rpush("rpushok", "1", "2", "3");
            assertEquals(3, k);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw e;
        }
    }

    @Test
    void shouldRpushAndRpushAgain() {
        try(Jedis jedis = new Jedis("127.0.0.1", 6379);) {
            jedis.lpush("rpushok1", "1", "2", "3");
            long k = jedis.rpush("rpushok1", "4", "5");
            assertEquals(5, k);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw e;
        }
    }

    @Test
    void shouldLrangeInCorrectOrderAfterMultipleRpush() {
        try(Jedis jedis = new Jedis("127.0.0.1", 6379);) {
            jedis.rpush("rpush2", "1", "2", "3");
            jedis.rpush("rpush2", "4", "5");
            List<String> out = jedis.lrange("rpush2", 0, 10);
            assertEquals(List.of("1", "2", "3", "4", "5"), out);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw e;
        }
    }
}
