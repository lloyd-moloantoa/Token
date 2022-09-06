package com.template.states;


import com.r3.corda.lib.tokens.contracts.types.TokenPointer;
import net.corda.core.contracts.LinearPointer;
import net.corda.core.contracts.UniqueIdentifier;
import com.r3.corda.lib.tokens.contracts.states.EvolvableTokenType;
import org.jetbrains.annotations.NotNull;

import com.template.contracts.Contract;
import net.corda.core.contracts.BelongsToContract;
import net.corda.core.identity.Party;

import java.util.Arrays;
import java.util.List;

// *********
// * State *
// *********
@BelongsToContract(FungibleTokenContract.class)
public class TokenFungible extends EvolvableTokenType {
    private final Party issuer;
    private String text = "This is fungible yo token (Mimicking fungible onership such as stocks)";
    private final int fractionDigits=0;
    private final UniqueIdentifier id;

    @ConstructorForDeserialization
    public TokenFungible(Party issuer,UniqueIdentifier id, String text) {
        this.issuer = issuer;
        this.id = id;
        this.text = text;
    }

    public TokenFungible(Party issuer) {
        this.issuer = issuer;
        this.id = new UniqueIdentifier();
    }

    public Party getIssuer() {
        return issuer;
    }

    public String getText() {
        return text;
    }

    public UniqueIdentifier getId() {
        return id;
    }

    @Override
    public int getFractionDigits() {
        return this.fractionDigits;
    }

    @NotNull
    @Override
    public List<Party> getMaintainers() {
        return Arrays.asList(issuer);
    }

    @NotNull
    @Override
    public UniqueIdentifier getLinearId() {
        return this.id;
    }

    /* This method returns a TokenPointer by using the linear Id of the evolvable state */
    public TokenPointer<TokenFungible> toPointer(){
        LinearPointer<TokenFungible> linearPointer = new LinearPointer<>(this.id, TokenFungible.class);
        return new TokenPointer<>(linearPointer, fractionDigits);
    }
}
