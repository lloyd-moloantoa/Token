package com.template.flows;

import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.contracts.StateAndContract;
import com.template.contracts.Contract;
import com.template.states.State;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.ProgressTracker;
import net.corda.core.contracts.Command;

import java.util.Arrays;
import java.util.Objects;

// *********
// * Flows *
// *********
@InitiatingFlow
@StartableByRPC
public class YoFlow extends FlowLogic<SignedTransaction> {
    private static final ProgressTracker.Step CREATING = new ProgressTracker.Step("Creating a new Token!");
    private static final ProgressTracker.Step SIGNING = new ProgressTracker.Step("Signing the token!");
    private static final ProgressTracker.Step VERIFYING = new ProgressTracker.Step("Verifying the token!");
    private static final ProgressTracker.Step FINALISING = new ProgressTracker.Step("Sending the token!") {
        @Nullable
        @Override
        public ProgressTracker childProgressTracker() {
            return FinalityFlow.tracker();
        }
    };

    ProgressTracker progressTracker = new ProgressTracker(
            CREATING,
            SIGNING,
            VERIFYING,
            FINALISING
    );

    @Nullable
    @Override
    public ProgressTracker getProgressTracker() {
        return progressTracker;
    }

    private final Party target;

    public Flow(Party target) {
        this.target = target;
    }

    @Suspendable
    @Override
    public SignedTransaction call() throws FlowException {
        progressTracker.setCurrentStep(CREATING);

        Party me = getOurIdentity();

        // Obtain a reference to a notary we wish to use.
        /** METHOD 1: Take first notary on network, WARNING: use for test, non-prod environments, and single-notary networks only!*
         *  METHOD 2: Explicit selection of notary by CordaX500Name - argument can by coded in flow or parsed from config (Preferred)
         *
         *  * - For production you always want to use Method 2 as it guarantees the expected notary is returned.
         */
        final Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0); // METHOD 1
        // final Party notary = getServiceHub().getNetworkMapCache().getNotary(CordaX500Name.parse("O=Notary,L=London,C=GB")); // METHOD 2

        Command<Contract.Commands.Send> command = new Command<Contract.Commands.Send>(new Contract.Commands.Send(), Arrays.asList(me.getOwningKey()));
        State state = new State(me, target);
        StateAndContract stateAndContract = new StateAndContract(state, YoContract.ID);
        TransactionBuilder utx = new TransactionBuilder(notary).withItems(stateAndContract, command);

        progressTracker.setCurrentStep(VERIFYING);
        utx.verify(getServiceHub());

        progressTracker.setCurrentStep(SIGNING);
        SignedTransaction stx = getServiceHub().signInitialTransaction(utx);

        progressTracker.setCurrentStep(FINALISING);
        FlowSession targetSession = initiateFlow(target);
        return subFlow(new FinalityFlow(stx, Arrays.asList(targetSession), Objects.requireNonNull(FINALISING.childProgressTracker())));
    }
}


