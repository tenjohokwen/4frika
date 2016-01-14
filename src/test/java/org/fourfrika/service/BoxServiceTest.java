package org.fourfrika.service;

import org.flywaydb.test.annotation.FlywayTest;
import org.flywaydb.test.dbunit.DBUnitSupport;
import org.fourfrika.Application;
import org.fourfrika.domain.Box;
import org.fourfrika.repository.BoxRepository;
import org.fourfrika.service.core.DBUnitTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Application.class})
public class BoxServiceTest extends DBUnitTest {

    @Autowired
    private BoxService boxService;

    @Autowired
    private BoxRepository boxRepository;

    @Test
    public void testUpdateBox() throws Exception {
        List<Box> boxes = (List<Box>) boxRepository.findAll();
        int initialCount = boxes.size();
        boxRepository.delete(5l);
        boxes = (List<Box>) boxRepository.findAll();
        assertTrue(boxes.size() == (initialCount - 1));
    }

    @Test
    @FlywayTest
    @DBUnitSupport(
            loadFilesForRun = { "INSERT", "/dbunit/dbunit_add_dirty_white.xml"})
    public void testDirtyWhite() throws Exception {
        List<Box> boxes = (List<Box>) boxRepository.findAll();
        assertThat(boxes.size()).isEqualTo(6);
    }

}