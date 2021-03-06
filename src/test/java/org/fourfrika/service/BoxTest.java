package org.fourfrika.service;

import com.google.common.util.concurrent.Uninterruptibles;
import org.flywaydb.test.annotation.FlywayTest;
import org.flywaydb.test.dbunit.DBUnitSupport;
import org.fourfrika.Application;
import org.fourfrika.domain.Box;
import org.fourfrika.repository.BoxRepository;
import org.fourfrika.service.core.DBUnitTestNg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.testng.annotations.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

@Test
@SpringApplicationConfiguration(classes = {Application.class})
public class BoxTest extends DBUnitTestNg {

    @Autowired
    private BoxService boxService;

    @Autowired
    private BoxRepository boxRepository;

    @FlywayTest
    public void testUpdateBox() throws Exception {
        List<Box> boxes = boxRepository.findAll();
        int initialCount = boxes.size();
        persistBox();
        boxRepository.delete(5L);
        boxes =  boxRepository.findAll();
        assertTrue(boxes.size() == (initialCount));
        boxService.delete(2L);
        Uninterruptibles.sleepUninterruptibly(3, TimeUnit.SECONDS);
    }

    @FlywayTest
    public void testAudit() throws Exception {
        persistBox();
    }

    @FlywayTest
    public void testAudit2() throws Exception {
        persistBox();
    }

    private void persistBox() {
        Box box = new Box();
        box.setLabel("blueLabel");
        box.setId(21L);
        boxRepository.save(box);
        Box boxx = boxRepository.getOne(21L);
        boxx.setLabel("blue_label");
        boxRepository.save(boxx);
    }

    @FlywayTest
    @DBUnitSupport(
            loadFilesForRun = { "INSERT", "/dbunit/dbunit_add_dirty_white.xml"})
    public void testDirtyWhite() throws Exception {
        List<Box> boxes = boxRepository.findAll();
        assertThat(boxes.size()).isEqualTo(6);
    }

}