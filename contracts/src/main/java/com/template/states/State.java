package com.template.states;

import com.template.contracts.Contract;
import net.corda.core.contracts.BelongsToContract;
import net.corda.core.contracts.ContractState;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import net.corda.core.serialization.ConstructorForDeserialization;
import net.corda.core.serialization.CordaSerializable;

import java.util.Arrays;
import java.util.List;

// *********
// * State *
// *********
@BelongsToContract(Contract.class)
public class State implements ContractState {

    private final Party origin;
    private final Party target;
    private final String text;

    @ConstructorForDeserialization
    public State(Party origin, Party target, String text) {
        this.origin = origin;
        this.target = target;
        this.text = text;
    }

    public State(Party origin, Party target) {
        this.origin = origin;
        this.target = target;
        this.text = "lloyd";
    }

    public Party getOrigin() {
        return origin;
    }

    public Party getTarget() {
        return target;
    }

    public String getText() {
        return text;
    }

    @NotNull
    @Override
    public List<AbstractParty> getParticipants() {
        return Arrays.asList(target);
    }

    @Override
    public String toString() {
        return origin.getName() + ": " + text;
    }
}