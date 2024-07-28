/*
 * Copyright (c) 2024 Marcelo Hashimoto
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */

/**
 * Exports a simple reflection library shared by
 * <a href="https://github.com/hashiprobr/sdx-java-dao">sdx-dao</a> and
 * <a href="https://github.com/hashiprobr/sdx-java-rest">sdx-rest</a>.
 */
module br.pro.hashi.sdx.dao {
    requires org.objenesis;

    exports br.pro.hashi.sdx.reflection;
}
