/*
 * SonarQube PHP Plugin
 * Copyright (C) 2010 SonarSource and Akram Ben Aissi
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.php.checks;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.GenericTokenType;
import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.squid.checks.SquidCheck;
import org.apache.commons.lang.StringUtils;
import org.sonar.check.BelongsToProfile;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.php.lexer.PHPLexer;
import org.sonar.php.parser.PHPGrammar;

import java.util.regex.Pattern;

@Rule(
  key = "S115",
  priority = Priority.MAJOR)
@BelongsToProfile(title = CheckList.SONAR_WAY_PROFILE, priority = Priority.MAJOR)
public class ConstantNameCheck extends SquidCheck<Grammar> {

  public static final String DEFAULT = "^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$";
  private Pattern pattern = null;
  private static final Pattern STRING_LITERAL_PATTERN = Pattern.compile(PHPLexer.STRING_LITERAL);

  @RuleProperty(
    key = "format",
    defaultValue = DEFAULT)
  String format = DEFAULT;


  @Override
  public void init() {
    pattern = Pattern.compile(format);
    subscribeTo(
      PHPGrammar.CLASS_CONSTANT_DECLARATION,
      PHPGrammar.FUNCTION_CALL_PARAMETER_LIST,
      PHPGrammar.CONSTANT_DECLARATION);
  }

  @Override
  public void visitNode(AstNode astNode) {
    if (isCallToDefine(astNode)) {
      checkConstantName(astNode, getFirstParameter(astNode));
    } else if (astNode.is(PHPGrammar.CLASS_CONSTANT_DECLARATION, PHPGrammar.CONSTANT_DECLARATION)) {
      for (AstNode constDec : astNode.getChildren(PHPGrammar.MEMBER_CONST_DECLARATION, PHPGrammar.CONSTANT_VAR)) {
        checkConstantName(constDec, constDec.getFirstChild(GenericTokenType.IDENTIFIER).getTokenOriginalValue());
      }
    }
  }

  private void checkConstantName(AstNode node, String constName) {
    if (constName != null && !pattern.matcher(constName).matches()) {
      getContext().createLineViolation(this, "Rename this constant \"{0}\" to match the regular expression {1}.", node, constName, format);
    }
  }

  private String getFirstParameter(AstNode astNode) {
    AstNode parameters = astNode.getFirstChild(PHPGrammar.PARAMETER_LIST_FOR_CALL);

    if (parameters != null) {
      String firstParam = parameters.getFirstChild().getTokenOriginalValue();
      if (STRING_LITERAL_PATTERN.matcher(firstParam).matches()) {
        return StringUtils.substring(firstParam, 1, firstParam.length() - 1);
      }
    }
    return null;
  }

  private static boolean isCallToDefine(AstNode node) {
    return node.is(PHPGrammar.FUNCTION_CALL_PARAMETER_LIST)
      && "define".equals(node.getPreviousAstNode().getTokenOriginalValue());
  }


}
