package com.template.contracts;

import com.template.states.State;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.Command;
import net.corda.core.contracts.Contract;
import net.corda.core.contracts.ContractState;
import net.corda.core.transactions.LedgerTransaction;
import org.jetbrains.annotations.NotNull;

import static net.corda.core.contracts.ContractsDSL.*;

import java.security.PublicKey;
import java.util.List;


// ************
// * Contract *
// ************
public class Contract implements Contract {
    // Used to identify our contract when building a transaction.
    public static final String ID = "com.template.contracts.Contract";

    // Contract code.
    @Override
    public void verify(@NotNull LedgerTransaction tx) throws IllegalArgumentException {
        CommandWithParties<Commands.Send> command = requireSingleCommand(tx.getCommands(), Commands.Send.class);
        requireThat(req -> {
            req.using("There can be no inputs when creating other parties", tx.getInputs().isEmpty());
            req.using("There must be one output", tx.getOutputs().size() == 1);
            YoState yo = tx.outputsOfType(YoState.class).get(0);
            req.using("No sending token to yourself!", !yo.getTarget().equals(yo.getOrigin()));
            req.using("The contract must be signed by the sender.", command.getSigners().contains(yo.getOrigin().getOwningKey()));
            return null;
        });
    }

    // Used to indicate the transaction's intent.
    public interface Commands extends CommandData {
        class Send implements Commands {}
    }
}