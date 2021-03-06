/* automatically converted from the Nick Parlante's excellent exercising site http://javabat.com/ */

package bat.bool2;

import plm.universe.bat.BatEntity;
import plm.universe.bat.BatTest;

public class NearTenEntity extends BatEntity {
    public void run(BatTest t) {
        t.setResult(nearTen((Integer) t.getParameter(0)));
    }

    /* BEGIN TEMPLATE */
    boolean nearTen(int num) {
        /* BEGIN SOLUTION */
        return (num % 10) <= 2 || (num % 10) >= 8;
		/* END SOLUTION */
    }
	/* END TEMPLATE */
}
