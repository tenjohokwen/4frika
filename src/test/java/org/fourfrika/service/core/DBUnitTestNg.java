package org.fourfrika.service.core;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import org.flywaydb.test.annotation.FlywayTest;
import org.flywaydb.test.dbunit.FlywayDBUnitTestExecutionListener;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

/**
 * Created by mokwen on 14.01.16.
 * Base class for Integration tests which require DBUnit.
 * If you are using only @FlywayTest (i.e without @DBUnitSupport), then you need 'FlywayTestExecutionListener' instead of  'FlywayDBUnitTestExecutionListener' .
 * Application context has to be configured as well.
 *
 */
@TestExecutionListeners(listeners = {DbUnitTestExecutionListener.class, DependencyInjectionTestExecutionListener.class, FlywayDBUnitTestExecutionListener.class})
@ActiveProfiles(value = {"test"})
@FlywayTest
public class DBUnitTestNg extends TestNgBaseTest{
}
