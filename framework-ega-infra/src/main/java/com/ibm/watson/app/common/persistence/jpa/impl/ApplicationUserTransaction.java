/* Copyright IBM Corp. 2015
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ibm.watson.app.common.persistence.jpa.impl;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.TransactionSynchronizationRegistry;
import javax.transaction.UserTransaction;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ibm.watson.app.common.persistence.impl.CloseableInitialContext;
import com.ibm.watson.app.common.persistence.jpa.ApplicationTransaction;
import com.ibm.watson.app.common.util.rest.MessageKey;

public class ApplicationUserTransaction implements ApplicationTransaction {
    private static final Logger logger = LogManager.getLogger();

    private UserTransaction t;
    private TransactionSynchronizationRegistry tsr;
    private boolean active = false;

    ApplicationUserTransaction() {
        try(CloseableInitialContext ctx = new CloseableInitialContext()) {
            t = (UserTransaction) ctx.lookup("java:comp/UserTransaction");
            tsr = (TransactionSynchronizationRegistry) ctx.lookup("java:comp/TransactionSynchronizationRegistry");
        } catch (Exception e) {
            // e.printStackTrace(System.out);
        }
    }

    @Override
    public void begin() {
        try {
            t.begin();
            active = true;
        } catch (SystemException | NotSupportedException e) {
            logger.warn(MessageKey.AQWEGA22102W_unable_begin_transaction.getMessage(), e);
            // e.printStackTrace(System.out);
        }
    }

    @Override
    public void commit() {
        try {
            t.commit();
            active = false;
        } catch (SystemException | RollbackException | HeuristicRollbackException | HeuristicMixedException e) {
            logger.warn(MessageKey.AQWEGA22101W_unable_commit_transaction.getMessage(), e);
            // e.printStackTrace(System.out);
        }
        try {
            if (tsr != null) {
                final int state = tsr.getTransactionStatus();
                if (state != Status.STATUS_NO_TRANSACTION)
                	logger.warn(MessageKey.AQWEGA22100W_commit_transaction_not_in_no_transaction_state_instead_1.getMessage(state));
            }
        } catch (Exception e) {
            // e.printStackTrace(System.out);
        }
    }

    @Override
    public boolean isActive() {
        try {
            if (tsr != null) {
                return tsr.getTransactionStatus() == Status.STATUS_ACTIVE;
            }
        } catch (Exception e) {
            // e.printStackTrace(System.out);
        }
        return active;
    }

    @Override
    public void rollback() {
        try {
            // t.rollback();
            t.setRollbackOnly();
            active = false;
        } catch (SystemException e) {
            logger.warn(MessageKey.AQWEGA22103W_unable_rollback_transaction.getMessage(), e);
            // e.printStackTrace(System.out);
        }
    }
}
