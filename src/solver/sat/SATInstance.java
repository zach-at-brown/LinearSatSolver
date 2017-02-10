package solver.sat;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A simple class to represent a SAT instance.
 */
public class SATInstance
{
  // The number of variables
  int numVars;

	// The number of clauses
  int numClauses;

	// The set of variables (variables are strictly positive integers)
  Set<Integer> vars = new HashSet<Integer>();

	// The list of clauses
  List<Set<Integer>> clauses = new ArrayList<Set<Integer>>();

  public SATInstance(int numVars, int numClauses)
  {
		this.numVars = numVars;
		this.numClauses = numClauses;
	}

  void addVariable(Integer literal)
  {
    this.vars.add( (literal < 0)? -1 * literal : literal);
  }

  void addClause(Set<Integer> clause)
  {
    this.clauses.add(clause);
  }

  //Removes clause by specified element at index
  void removeClause(Integer index)
  {
    this.clauses.remove(this.clauses.get(index));
    this.numClauses--;
  }

  //Removes all occurrences of a literal from all clauses
  //eg: removing -1 only removes occurrences of -1 from all clauses.
  // 1 may still be in clauses
  void removeLiteral(Integer literal)
  {
    for(int c = 0; c < this.clauses.size(); c++ )
    {
      //Check if clause contains literal
      if(this.clauses.get(c).contains(literal))
      {
        //Remove literal
        this.clauses.get(c).remove(literal);
      }
    }
  }

  public String toString()
  {
    StringBuffer buf = new StringBuffer();
    buf.append("Number of variables: " + numVars + "\n");
		buf.append("Number of clauses: " + numClauses + "\n");
    buf.append("Variables: " + this.vars.toString() + "\n");
    for(int c = 0; c < this.clauses.size(); c++)
			buf.append("Clause " + c + ": " + this.clauses.get(c).toString() + "\n");
    return buf.toString();
  }
}
