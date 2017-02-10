package solver.sat;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple class to represent a node, with which DPLL branching will occur.
 */
public class Node
{
  //Variable to be branched on
  Integer var;

  // Assignment of current variable of branchingg
  Boolean var_assign;

  //Hashmap of variable assignments until this point in branching
  HashMap<Integer, Boolean> assignments = new HashMap<Integer, Boolean>();


  //Constructor for Node Object class
  public Node(int var, boolean var_assign, HashMap<Integer, Boolean> assignments)
  {
		this.var = var;
		this.var_assign = var_assign;
    this.assignments = assignments;
  }

}
