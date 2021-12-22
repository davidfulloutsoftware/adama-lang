package org.adamalang.mysql.frontend;

import org.adamalang.mysql.Base;
import org.adamalang.mysql.BaseConfig;
import org.adamalang.mysql.BaseConfigTests;
import org.adamalang.runtime.exceptions.ErrorCodeException;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;
import java.util.List;

public class FrontendTests {

    @Test
    public void users() throws Exception {
        BaseConfig baseConfig = BaseConfigTests.getLocalIntegrationConfig();
        try (Base base = new Base(baseConfig)) {
            ManagementInstaller installer = new ManagementInstaller(base);
            try {
                installer.install();
                Assert.assertEquals(1, Users.getOrCreateUserId(base, "x@x.com"));
                Assert.assertEquals(1, Users.getOrCreateUserId(base, "x@x.com"));
                Users.addKey(base, 1, "key", new Date(System.currentTimeMillis() + 1000 * 60));
                Assert.assertEquals("key", Users.listKeys(base, 1).get(0));
                Users.removeAllKeys(base, 1);
                Assert.assertEquals(0, Users.listKeys(base, 1).size());
            } finally {
                installer.uninstall();
            }
        }
    }

    @Test
    public void spaces() throws Exception {
        BaseConfig baseConfig = BaseConfigTests.getLocalIntegrationConfig();
        try (Base base = new Base(baseConfig)) {
            ManagementInstaller installer = new ManagementInstaller(base);
            try {
                installer.install();
                int alice = Users.getOrCreateUserId(base, "alice@x.com");
                int bob = Users.getOrCreateUserId(base, "bob@x.com");
                Assert.assertEquals(1, Spaces.createSpace(base, alice, "space1"));
                Assert.assertEquals(1, Spaces.getSpaceId(base, "space1").id);
                Assert.assertEquals(2, Spaces.createSpace(base, bob, "space2"));
                Assert.assertEquals(2, Spaces.getSpaceId(base, "space2").id);
                Assert.assertEquals("{}", Spaces.getPlan(base, 1));
                Assert.assertEquals("{}", Spaces.getPlan(base, 2));
                Spaces.setPlan(base, 1, "{\"x\":1}");
                Spaces.setPlan(base, 2, "{\"x\":2}");
                Spaces.setBilling(base, 1, "fixed50");
                Spaces.setBilling(base, 2, "open");
                Assert.assertEquals("{\"x\":1}", Spaces.getPlan(base, 1));
                Assert.assertEquals("{\"x\":2}", Spaces.getPlan(base, 2));
                {
                    List<Spaces.Item> ls1 = Spaces.list(base, alice, null, 5);
                    List<Spaces.Item> ls2 = Spaces.list(base, bob, null, 5);
                    Assert.assertEquals(1, ls1.size());
                    Assert.assertEquals(1, ls2.size());
                    Assert.assertEquals("space1", ls1.get(0).name);
                    Assert.assertEquals("space2", ls2.get(0).name);
                    Assert.assertEquals("owner", ls1.get(0).callerRole);
                    Assert.assertEquals("owner", ls2.get(0).callerRole);
                    Assert.assertEquals("fixed50", ls1.get(0).billing);
                    Assert.assertEquals("open", ls2.get(0).billing);
                }
                Spaces.setRole(base, 2, alice, Role.Developer);
                {
                    List<Spaces.Item> ls1 = Spaces.list(base, alice, null, 5);
                    List<Spaces.Item> ls2 = Spaces.list(base, bob, null, 5);
                    Assert.assertEquals(2, ls1.size());
                    Assert.assertEquals(1, ls2.size());
                    Assert.assertEquals("space1", ls1.get(0).name);
                    Assert.assertEquals("space2", ls1.get(1).name);
                    Assert.assertEquals("space2", ls2.get(0).name);
                    Assert.assertEquals("owner", ls1.get(0).callerRole);
                    Assert.assertEquals("developer", ls1.get(1).callerRole);
                    Assert.assertEquals("owner", ls2.get(0).callerRole);
                }
                Spaces.changePrimaryOwner(base, 1, alice, bob);
                {
                    List<Spaces.Item> ls1 = Spaces.list(base, alice, null, 5);
                    List<Spaces.Item> ls2 = Spaces.list(base, bob, null, 5);
                    Assert.assertEquals(1, ls1.size());
                    Assert.assertEquals(2, ls2.size());
                    Assert.assertEquals("space2", ls1.get(0).name);
                    Assert.assertEquals("space1", ls2.get(0).name);
                    Assert.assertEquals("space2", ls2.get(1).name);
                    Assert.assertEquals("developer", ls1.get(0).callerRole);
                    Assert.assertEquals("owner", ls2.get(0).callerRole);
                    Assert.assertEquals("owner", ls2.get(1).callerRole);
                }
                Spaces.setRole(base, 2, alice, Role.None);
                {
                    List<Spaces.Item> ls1 = Spaces.list(base, alice, null, 5);
                    List<Spaces.Item> ls2 = Spaces.list(base, bob, null, 5);
                    Assert.assertEquals(0, ls1.size());
                    Assert.assertEquals(2, ls2.size());
                    Assert.assertEquals("space1", ls2.get(0).name);
                    Assert.assertEquals("space2", ls2.get(1).name);
                }

                try {
                    Spaces.createSpace(base, alice, "space1");
                    Assert.fail();
                } catch (ErrorCodeException ex) {
                    Assert.assertEquals(679948, ex.code);
                }
                try {
                    Spaces.getSpaceId(base, "space3");
                    Assert.fail();
                } catch (ErrorCodeException ex) {
                    Assert.assertEquals(625678, ex.code);
                }
                try {
                    Spaces.getPlan(base, 5);
                    Assert.fail();
                } catch (ErrorCodeException ex) {
                    Assert.assertEquals(609294, ex.code);
                }
            } finally {
                installer.uninstall();
            }
        }
    }
}
