package org.msk86.ygoroid.newaction.actionimpl;

import org.msk86.ygoroid.newop.Operation;

public class UnSelectAction extends BaseAction {
    public UnSelectAction(Operation operation) {
        super(operation);
    }

    @Override
    public void execute() {
        duel.unSelect();
    }
}