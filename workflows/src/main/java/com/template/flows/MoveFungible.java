package com.template.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.r3.corda.lib.tokens.workflows.flows.rpc.MoveFungibleTokens;
import com.template.states.CarTokenState;
import net.corda.core.contracts.Amount;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.InitiatingFlow;
import net.corda.core.flows.StartableByRPC;
import net.corda.core.identity.Party;
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.vault.QueryCriteria;
import net.corda.core.transactions.SignedTransaction;

import java.util.Arrays;
import java.util.UUID;

/**
 * Designed initiating node : Company
 * This flow is designed for company to move the issued tokens of stock to the a shareholder node.
 * To make it more real, we can modify it such that the shareholder exchanges some fiat currency for some stock tokens.
 */
@InitiatingFlow
@StartableByRPC
public class MoveFungible {

    @InitiatingFlow
    @StartableByRPC
    public static class MoveFungibleInitiator extends FlowLogic<String> {
        private final String tokenId;
        private final Long quantity;
        private final Party recipient;

        public MoveFungibleInitiator(String tokenId, Long quantity, Party recipient) {
            this.tokenId = tokenId;
            this.quantity = quantity;
            this.recipient = recipient;
        }

        @Override
        @Suspendable
        public String call() throws FlowException {

            /* Get the UUID from the houseId parameter */
            UUID uuid = UUID.fromString(tokenId);

            /* Fetch the house state from the vault using the vault query */
            QueryCriteria.LinearStateQueryCriteria queryCriteria = new QueryCriteria.LinearStateQueryCriteria
                    (null,Arrays.asList(uuid),null, Vault.StateStatus.UNCONSUMED);


            StateAndRef<TokenFungible> FungibleStateAndRef = getServiceHub().getVaultService().
                    queryBy(TokenFungible.class, queryCriteria).getStates().get(0);

            TokenFungible State = FungibleStateAndRef.getState().getData();

            // With the pointer, we can get the create an instance of transferring Amount
            Amount<TokenType> amount = new Amount(quantity, State.toPointer());

            //Use built-in flow for move tokens to the recipient
            SignedTransaction stx = subFlow(new MoveFungibleTokens(amount, recipient));

            return "\nMove "+this.quantity +" Fungible Tokens to "
                    + this.recipient.getName().getOrganisation() + ".\nTransaction ID: "+stx.getId();

        }
    }
}