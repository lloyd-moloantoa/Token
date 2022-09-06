package com.template.contracts;

import com.r3.corda.lib.tokens.contracts.EvolvableTokenContract;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.CommandWithParties;
import net.corda.core.contracts.Contract;
import net.corda.core.transactions.LedgerTransaction;
import org.jetbrains.annotations.NotNull;
import com.template.states.TokenFungible;

import static net.corda.core.contracts.ContractsDSL.*;

// ************
// * Contract *
// ************
// Contract and state.
public class FungibleTokenContract extends EvolvableTokenContract implements Contract {
    @Override
    public void verify(@NotNull LedgerTransaction tx) throws IllegalArgumentException {
        TokenFungible outputState = (TokenFungible) tx.getOutput(0);
        if(!(tx.getCommand(0).getSigners().contains(outputState.getIssuer().getOwningKey())))
            throw new IllegalArgumentException("Issuer Signature Required");
    }

    @Override
    public void additionalCreateChecks(@NotNull LedgerTransaction tx) {
        // Write contract validation logic to be performed while creation of token
    }

    @Override
    public void additionalUpdateChecks(@NotNull LedgerTransaction tx) {
        // Write contract validation logic to be performed while updation of token
    }
}