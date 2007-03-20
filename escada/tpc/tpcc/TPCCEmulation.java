/*
 * TPCC Client
 * Copyright (C) 2006 University of Minho
 * See http://gorda.di.uminho.pt/ for more information.
 *
 * Partially funded by the European Union Framework Programme for
 * Research and Technological Development, thematic priority
 * Information Society and Media, project GORDA (004758).
 * 
 * Contributors:
 *  - Rui Oliveira <rco@di.uminho.pt>
 *  - Jose Orlando Pereira <jop@di.uminho.pt>
 *  - Antonio Luis Sousa <als@di.uminho.pt>
 *  - Alfranio Tavares Correia Junior <alfranio@lsd.di.uminho.pt> 
 *  - Luis Soares <los@di.uminho.pt>
 *  - Ricardo Manuel Pereira Vilaca <rmvilaca@di.uminho.pt>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301,
 * USA.
 */


package escada.tpc.tpcc;

import org.apache.log4j.Logger;

import escada.tpc.common.Emulation;
import escada.tpc.common.StateObject;
import escada.tpc.common.util.RandGen;

/**
 * It extends the emulation class defining some methods according to the TPC-C
 * definition. Specifically, it defines the thinktime, keyingtime, process and
 * processIncrement.
 * 
 * @see Emulation
 */
public class TPCCEmulation extends Emulation {
	Logger logger = Logger.getLogger(TPCCEmulation.class);

	StateObject curTrans = null;

	public void initialize() {
	}

	public long thinkTime() {
		long r = RandGen.negExp(getRandom(), curTrans.getThinkTime() * 1000,
				0.36788, curTrans.getThinkTime() * 1000, 4.54e-5, curTrans
						.getThinkTime() * 1000);
		return (r);
	}

	public long keyingTime() {
		return (curTrans.getKeyingTime());
	}

	public void process(String hid) {
		try {
			while ((getMaxTransactions() == -1) || (getMaxTransactions() > 0)) {
				if (isFinished()) {
					logger.info("Client is returning.");
					return;
				}
				curTrans = getStateTransition().nextState();

				setKeyingTime(keyingTime());
				setThinkTime(getKeyingTime() + thinkTime());

				if (Emulation.getStatusThinkTime()) {
					Thread.sleep(getThinkTime());
				}

				curTrans.requestProcess(this, hid);

				curTrans.postProcess(this, hid);
			}
		} catch (java.lang.InterruptedException it) {
		} catch (java.lang.Exception ex) {
			logger.fatal("Unexpected error. Something bad happend.");
			ex.printStackTrace(System.err);
			System.exit(-1);
		}
	}

	public Object processIncrement(String hid) {
		Object trans = null;
		try {
			if (isFinished()) {
				logger.info("Client is returning.");
				return (trans);
			}
			curTrans = getStateTransition().nextState();

			setKeyingTime(keyingTime());
			setThinkTime(getKeyingTime() + thinkTime());

			trans = curTrans.requestProcess(this, hid);

			curTrans.postProcess(this, hid);
		} catch (java.lang.Exception ex) {
			logger.fatal("Unexpected error. Something bad happend.");
			ex.printStackTrace(System.err);
			System.exit(-1);
		}

		return (trans);
	}
}
// arch-tag: d08fc9ef-d774-4095-a9c0-41c500320bda
