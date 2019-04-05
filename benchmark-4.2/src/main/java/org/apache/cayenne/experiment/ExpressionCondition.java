package org.apache.cayenne.experiment;

import org.apache.cayenne.configuration.ConfigurationNodeVisitor;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.util.XMLEncoder;

public class ExpressionCondition extends DbJoinCondition {

    private Expression expression;

    public void addExpression(Expression expression) {
        if(expression != null) {
            this.expression = this.expression.andExp(expression);
        }
        this.expression = expression;
    }

    public Expression getExpression() {
        return expression;
    }

    @Override
    public void encodeAsXML(XMLEncoder encoder, ConfigurationNodeVisitor delegate) {

    }

    @Override
    public <T> T accept(JoinVisitor<T> joinVisitor) {
        return joinVisitor.visit(expression);
    }
}
