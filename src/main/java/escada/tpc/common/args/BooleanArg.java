/*
 * Copyright 2013 Universidade do Minho
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software   distributed under the License is distributed on an "AS IS" BASIS,   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and limitations under the License.
 */

package escada.tpc.common.args;

public class BooleanArg extends Arg {
	public boolean flag = false;

	public BooleanArg(String arg, String name, String desc) {
		super(arg, name, desc, false, false);
	}

	public BooleanArg(String arg, String name, String desc, ArgDB db) {
		super(arg, name, desc, false, false, db);
	}

	public BooleanArg(String arg, String name, String desc, boolean def) {
		super(arg, name, desc, false, true);
		flag = def;
	}

	public BooleanArg(String arg, String name, String desc, boolean def,
			ArgDB db) {
		super(arg, name, desc, false, true, db);
		flag = def;
	}

	// Customize to parse arguments.
	protected int parseMatch(String[] args, int a) throws Arg.Exception {
		if (a == args.length) {
			throw new Arg.Exception("Boolean argument missing value.", a);
		}

		char ch = args[a].charAt(0);

		switch (ch) {
		case '0': // zero
		case 'F': // false
		case 'f':
		case 'd': // disable
		case 'D':
		case 'n': // no
		case 'N':
			flag = false;
			break;
		case '1': // one
		case 'T': // true
		case 't':
		case 'e': // enable
		case 'E':
		case 'y': // yes
		case 'Y':
			flag = true;
			break;
		default:
			throw new Arg.Exception("Unable to parse flag (" + args[a] + ").",
					a);
		}

		return (a + 1);
	}

	public String value() {
		return ("" + flag);
	}
}

