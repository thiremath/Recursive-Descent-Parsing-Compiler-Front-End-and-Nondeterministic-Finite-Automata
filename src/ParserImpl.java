
public class ParserImpl extends Parser {

    /*
     * Implements a recursive-descent parser for the following CFG:
     * 
     * T -> F AddOp T              { if ($2.type == TokenType.PLUS) { $$ = new PlusExpr($1,$3); } else { $$ = new MinusExpr($1, $3); } }
     * T -> F                      { $$ = $1; }
     * F -> Lit MulOp F            { if ($2.type == TokenType.Times) { $$ = new TimesExpr($1,$3); } else { $$ = new DivExpr($1, $3); } }
     * F -> Lit                    { $$ = $1; }
     * Lit -> NUM                  { $$ = new FloatExpr(Float.parseFloat($1.lexeme)); }
     * Lit -> LPAREN T RPAREN      { $$ = $2; }
     * AddOp -> PLUS               { $$ = $1; }
     * AddOp -> MINUS              { $$ = $1; }
     * MulOp -> TIMES              { $$ = $1; }
     * MulOp -> DIV                { $$ = $1; }
     */
    @Override
    public Expr do_parse() throws Exception {
        return parseTerm();
    }

    // Helper method for non-terminal Term
    private Expr parseTerm() throws Exception {
        Expr left = parseFactor();
        if (peek(TokenType.PLUS, 0) || peek(TokenType.MINUS, 0)) {
            Token op = consume(peek(TokenType.PLUS, 0) ? TokenType.PLUS : TokenType.MINUS);
            Expr right = parseTerm();
            return op.ty == TokenType.PLUS ? new PlusExpr(left, right) : new MinusExpr(left, right);
        }
        return left;
    }

    // Helper method for non-terminal Factor
    private Expr parseFactor() throws Exception {
        Expr left = parseLiteral();
        if (peek(TokenType.TIMES, 0) || peek(TokenType.DIV, 0)) {
            Token op = consume(peek(TokenType.TIMES, 0) ? TokenType.TIMES : TokenType.DIV);
            Expr right = parseFactor();
            return op.ty == TokenType.TIMES ? new TimesExpr(left, right) : new DivExpr(left, right);
        }
        return left;
    }

    // Helper method for non-terminal Literal
    private Expr parseLiteral() throws Exception {
        if (peek(TokenType.NUM, 0)) {
            Token numToken = consume(TokenType.NUM);
            return new FloatExpr(Float.parseFloat(numToken.lexeme));
        } else if (peek(TokenType.LPAREN, 0)) {
            consume(TokenType.LPAREN);
            Expr innerExpr = parseTerm();
            consume(TokenType.RPAREN);
            return innerExpr;
        } else {
            throw new Exception("Expected a number or a parenthesis at " + tokens.elem.lexeme);
        }
    }

}
