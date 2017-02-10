package solver.sat;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.Stack;




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

  Stack<Node> branches = new Stack<Node>();

  //Hashmap of variable assignments
  HashMap<Integer, Boolean> globalAssignments = new HashMap<Integer, Boolean>();

  // The list of clauses
  List<Set<Integer>> clauses = new ArrayList<Set<Integer>>();
  List<Set<Integer>> originalClauses = new ArrayList<Set<Integer>>();

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

  //NOTE: I know we said we need this, but I'm not sure we actually do
  //Removes clause by specified element at index
  void removeClause(Integer index)
  {
    this.clauses.remove(this.clauses.get(index));
    this.numClauses--;
  }

  void removeClausesContaining(Integer literal)
  {
    for(int c = clauses.size()-1; c >= 0; c-- )
    {
      Set<Integer> thisClause = clauses.get(c);
      //Check if clause contains literal
      if(thisClause.contains(literal))
      {
	clauses.remove(thisClause);
	numClauses--;
      }
    }

  	
  
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

  
  protected boolean eliminatePureVariables(){
   boolean foundPure = false; 
   Set<Integer> usedVars = new HashSet<Integer>();

	 
   for (int i = 0; i < numClauses; i++)
   {
	usedVars.addAll(clauses.get(i));
   }

   Iterator<Integer> setIterator = usedVars.iterator();
   while (setIterator.hasNext())
  {
	Integer thisInt = setIterator.next();
	if (!usedVars.contains(-thisInt))
	{
		setVariable(thisInt);
		System.out.println("no opp: " + thisInt);
		foundPure = true;
	}
  }

    return foundPure; 
  }


  protected boolean eliminateFirstUnitClause(){
   boolean foundUnit = false; 
	 
   for (int i = 0; i < numClauses; i++){
	Set<Integer> IthClause = clauses.get(i);
	if(IthClause.size() == 1){
	  //System.out.println("unit clause at index" + i);
	  Integer entryToSet = (Integer)(IthClause.toArray())[0];
	  System.out.println("assigning " + Math.abs(entryToSet) + " to be " + (entryToSet > 0)); 
	  setVariable(entryToSet);
	  foundUnit = true;
	  break;
	  
	}
    }

    return foundUnit; 
  }

  //NOTE: this method may have to change substantially to support branching
  private void setVariable(Integer varToAssign){ 
     //mark the choice in our instance
     if(branches.empty())
     {
     	globalAssignments.put(Math.abs(varToAssign), varToAssign > 0);
     }
     else
     {
	branches.peek().assignments.put(Math.abs(varToAssign), varToAssign > 0);
     }
     //propagate changes to other clauses
     removeLiteral(-1 * varToAssign);
     removeClausesContaining(varToAssign);
  }

  
/**
* Description functions
*/
  public void describeAssignments()
  {
	for (Map.Entry<Integer, Boolean> entry : globalAssignments.entrySet())
	{
		System.out.println(entry.getKey() + " : " + entry.getValue());
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
