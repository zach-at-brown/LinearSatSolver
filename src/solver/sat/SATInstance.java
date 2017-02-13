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
    if(branches.empty())
    {
      setVariable(thisInt,null);
    }
    else
    {
      setVariable(thisInt, branches.peek());
    }

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

    if(branches.empty())
    {
      setVariable(entryToSet,null);
    }
    else
    {
      setVariable(entryToSet, branches.peek());
    }

	  foundUnit = true;
	  break;

	}
    }

    return foundUnit;
  }

  //NOTE: this method may have to change substantially to support branching
  private void setVariable(Integer varToAssign, Node currNode){
     //mark the choice in our instance
     if(branches.empty())
     {
     	globalAssignments.put(Math.abs(varToAssign), varToAssign > 0);
     }
     else
     {
       currNode.assignments.put(Math.abs(varToAssign), varToAssign > 0);
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

  //Apply variable assignments up until a bad decision is made
  public List<Set<Integer>> applyAssignments(Node currNode)
  {
    List<Set<Integer>> clausesToReturn = originalClauses;
    Iterator<Integer> setIterator = null;
    for(int c=0; c<clausesToReturn.size(); c++ )
    {
      setIterator = currNode.assignments.keySet().iterator();
      while(setIterator.hasNext())
      {
        Integer currKey = Math.abs(setIterator.next());
        if(clausesToReturn.get(c).contains(currKey))
        {
          //If variable is assigned True
          if(currNode.assignments.get(currKey))
          {
            removeClause(c);
            continue;
          }
          else{ //Variable is assigned False
            //If !Variable is found anywhere in same clause, remove clause
            if(clausesToReturn.get(c).contains(-1*currKey))
            {
              removeClause(c);
              continue;
            }
            else  //no choice but to remove variable from clause
            {
              clausesToReturn.remove(currKey);
            }
          }
        }
      }
    }
    return clausesToReturn;
  }

  //Checks if there are any empty clauses
  //NOTE : Probably need to append more conditions
  public boolean isUnsat()
  {
    for (int i = 0; i < numClauses; i++)
    {
      if(clauses.get(i).isEmpty())
      {
        return true;
      }
    }
    return false;
  }

  public boolean solve()
  {
    while (true)
		{
      //Check first rule
			if(eliminateFirstUnitClause())
			{
				continue;
			}
      //Check second rule
			if(eliminatePureVariables())
      {
				continue;
			}
      //Check for empty clauses
			if(numClauses == 0)
			{
				return true;
			}
      //Checks if current branch is UNSAT. Tries opposite assignment if true
      //Then backtracks
      if(isUnsat())
      {
        if(branches.empty())
        {
          return false;
        }

        Node lastDecision = branches.pop();
        System.out.println("Bad decision : " + lastDecision.var + lastDecision.var_assign);
        Node current = branches.peek();
        setVariable(lastDecision.var, lastDecision);
        continue;
      }
      else
      {
        Set<Integer> unassignedVars = vars;
        Node newGuess = new Node();
        if(branches.empty())
        {
          unassignedVars.removeAll(globalAssignments.keySet());
          newGuess = new Node(unassignedVars.iterator().next(), false, globalAssignments);
        }
        else
        {
          unassignedVars.removeAll(branches.peek().assignments.keySet());
          newGuess = new Node(unassignedVars.iterator().next(), false, branches.peek().assignments);
        }

        System.out.println("About to assign true to " + newGuess.var);
        setVariable(newGuess.var, newGuess);

        branches.push(newGuess);
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
