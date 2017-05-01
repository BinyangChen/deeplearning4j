package org.deeplearning4j.datasets.iterator;

import org.deeplearning4j.datasets.iterator.parallel.JointParallelDataSetIterator;
import org.deeplearning4j.datasets.iterator.tools.SimpleVariableGenerator;
import org.junit.Test;
import org.nd4j.linalg.dataset.api.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.iterator.enums.InequalityHandling;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author raver119@gmail.com
 */
public class JointParallelDataSetIteratorTest {

    /**
     * Simple test, checking datasets alignment. They all should have the same data for the same cycle
     *
     *
     * @throws Exception
     */
    @Test
    public void testJointIterator1() throws Exception {
        DataSetIterator iteratorA = new SimpleVariableGenerator(119, 100, 32, 100, 10);
        DataSetIterator iteratorB = new SimpleVariableGenerator(119, 100, 32, 100, 10);

        JointParallelDataSetIterator jpdsi = new JointParallelDataSetIterator.Builder(InequalityHandling.STOP_EVERYONE)
                .addSourceIterator(iteratorA)
                .addSourceIterator(iteratorB)
                .build();

        int cnt = 0;
        int example = 0;
        while (jpdsi.hasNext()) {
            DataSet ds = jpdsi.next();
            assertNotNull("Failed on iteration " + cnt, ds);


            assertEquals("Failed on iteration " + cnt,(double) example, ds.getFeatures().meanNumber().doubleValue(), 0.001);
            assertEquals("Failed on iteration " + cnt, (double) example + 0.5, ds.getLabels().meanNumber().doubleValue(), 0.001);

            cnt++;
            if (cnt % 2 == 0)
                example++;
        }

        assertEquals(100, example);
        assertEquals(200, cnt);
    }

    @Test
    public void testJointIterator2() throws Exception {
        DataSetIterator iteratorA = new SimpleVariableGenerator(119, 200, 32, 100, 10);
        DataSetIterator iteratorB = new SimpleVariableGenerator(119, 100, 32, 100, 10);

        JointParallelDataSetIterator jpdsi = new JointParallelDataSetIterator.Builder(InequalityHandling.PASS_NULL)
                .addSourceIterator(iteratorA)
                .addSourceIterator(iteratorB)
                .build();

        int cnt = 0;
        int example = 0;
        while (jpdsi.hasNext()) {
            DataSet ds = jpdsi.next();
            if (cnt < 200)
                assertNotNull("Failed on iteration " + cnt, ds);

            if (cnt % 2 == 2) {
                assertEquals("Failed on iteration " + cnt, (double) example, ds.getFeatures().meanNumber().doubleValue(), 0.001);
                assertEquals("Failed on iteration " + cnt, (double) example + 0.5, ds.getLabels().meanNumber().doubleValue(), 0.001);
            }


            cnt++;
            if (cnt % 2 == 0)
                example++;
        }

        assertEquals(100, example);
        assertEquals(300, cnt);
    }
}
