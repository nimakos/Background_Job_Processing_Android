package com.example.vangelis.connectingdevices;

import android.support.test.runner.AndroidJUnit4;
import com.example.vangelis.connectingdevices.execute_tasks.ExecutePrimes;
import com.example.vangelis.connectingdevices.times.Op;
import com.example.vangelis.connectingdevices.times.TimingUtils;
import com.example.vangelis.connectingdevices.calculations.PrimeUtils;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {


  /*  @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.example.vangelis.connectingdevices", appContext.getPackageName());
    }

    @Test
    public void testSums() {
        for (int i=1; i<5; i++) {
            int arraySize = (int)Math.pow(10, i);
            Double[] nums = MathUtils.randomNums2(arraySize);
            double sum1 = MathUtils.arraySum(nums);
            double sum2 = MathUtils.arraySumParallel(nums);
            assertThat(sum1, is(closeTo(sum2, 0.000001)));
        }
    }*/

    /*@Test
    public void myTest() {

        final String message1 = "Sequential sum of %,d numbers is %,.4f.";
        final String message2 = "Parallel sum of %,d numbers is %,.4f.";
        for (int i = 3; i < 7; i++) {
            int arraySize = (int) Math.pow(10, i) / 2;
            Double[] nums = MathUtils.randomNums2(arraySize);
            TimingUtils.timeOp(new Op() {
                @Override
                public String runOp() {
                    double sum = MathUtils.arraySum(nums);
                    return (String.format(message1, arraySize, sum));
                }
            });
            TimingUtils.timeOp(new Op() {
                @Override
                public String runOp() {
                    double sum = MathUtils.arraySumParallel(nums);
                    return (String.format(message2, arraySize, sum));
                }
            });
        }
    }*/

    @Test
    public void myTest() {
        /*String message1 = "Sequential sum of %,d numbers is %,.4f.";
        String message2 = "Parallel sum of %,d numbers is %,.4f.";
        for (int i = 3; i < 9; i++) {
            int arraySize = (int) Math.pow(10, i) / 2;
            double[] nums = MathUtils1.randomDoubles(arraySize);
            TimingUtils.timeOp(new Op() {
                @Override
                public String runOp() {
                    double sum = MathUtils1.arraySum(nums);
                    return (String.format(message1, arraySize, sum));
                }
            });
            TimingUtils.timeOp(new Op() {
                @Override
                public String runOp() {
                    double sum = MathUtils1.arraySumParallel(nums);
                    return (String.format(message2, arraySize, sum));
                }
            });
        }*/

        String message3 = "Sequential sum of %,d numbers is %d.";
        String message4 = "Parallel primes sum of %,d numbers is %d.";
        String message5 = "Concurrent primes sum of %,d numbers is %d.";
        int number = 100_000_000;
        int [] nums = PrimeUtils.randomIntegers(number);
        TimingUtils.timeOp(new Op() {
            @Override
            public String runOp() {
                long sum = ExecutePrimes.arrayOfPrimesSumSerial(nums);
                return (String.format(message3, number, sum));
            }
        });

        TimingUtils.timeOp(new Op() {
            @Override
            public String runOp() {
                long sum = ExecutePrimes.arrayOfPrimesSumParallel(nums);
                return (String.format(message4, number, sum));
            }
        });

        TimingUtils.timeOp(new Op() {
            @Override
            public String runOp() {
                long sum = ExecutePrimes.arrayOfPrimesSumConcurrent(nums);
                return (String.format(message5, number, sum));
            }
        });

    }
}
