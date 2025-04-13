@Override
public TokenDefinition visitByteSizeArg(DirectivesParser.ByteSizeArgContext ctx) {
    String text = ctx.getText(); // Example: "10MB"
    ByteSize byteSize = new ByteSize(text); // You already created this in 3b
    return new TokenDefinition(byteSize);   // Wrap in TokenDefinition
}

@Override
public TokenDefinition visitTimeDurationArg(DirectivesParser.TimeDurationArgContext ctx) {
    String text = ctx.getText(); // Example: "150ms"
    TimeDuration timeDuration = new TimeDuration(text); // From 3b
    return new TokenDefinition(timeDuration);
}
