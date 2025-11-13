# CM-Treap
"Concurrent M-Treap with multivalued nodes via logical ordering"
M-Treap (Most accessed elements towards the root) 
Most recently accessed elements towards the root are supported by the splay tree, and M-Treap is a concept where the most accessed elements are towards the root.
Working:
1.	Initially, the priority of all newly created fat-nodes will be zero rather than having a random priority like in a traditional Treap.
2.	The priority of the fat-nodes increases by one each time a contains operation is performed on them. The fat-nodes with increased priority move up towards the root until their parent's priority is greater than the current fat-node's priority.
3.	The priority of a fat-node is set to zero if we want to delete an empty fat-node. The physical deletion of the fat-node is postponed until the node is automatically pushed down towards the leaves in the process of rebalancing the tree as the priorities of the other fat-nodes grow.
4.	The fat-node to be deleted, having priority zero, is pushed down towards the leaves until it has a single child or no children.
5.	A dedicated thread runs continuously using the logical ordering of the tree. This patrolling thread identifies empty fat-nodes with priority zero and completes the job of physically deleting them.
Key factors of this approach:
1.	The priority of all fat-nodes will be equal during the creation of a new fat-node.
2.	The priority of the nodes increases based on the number of times the node is accessed using the contains operation.
3.	No rebalancing of the tree is required during insert or delete operations.
4.	Lazy deletion with a dedicated patrolling thread helps in avoiding the rebalancing of the tree.
5.	As the most accessed elements are towards the root, contention is well handled by the ReentrantReadWriteLock class, where multiple threads can acquire a read lock simultaneously. The ConcurrentHashMap object present in each fat-node allows multiple threads to access the node concurrently.



## Running the Concurrent M-Treap

### Compilation
```bash
javac M_Treap_Mytest2.java

### Execution

```bash
java M_Treap_Mytest2 8 200000 60000 4 35 70

```

### Parameter Description

| Parameter                     | Meaning                                                                                           |
| ----------------------------- | ------------------------------------------------------------------------------------------------- |
| 8                         | Number of concurrent threads                                                                      |
| 200000                    | Range (domain) of input values                                                                    |
| 60000                     | Duration of execution in milliseconds (≈60 seconds)                                               |
| 4                         | Multivalued(fatnode)size.                                                                              |
| 35                        | Percentage of `contains()` operations                                                             |
| 70                        | Cumulative percentage for `contains()` + `insert()` operations (i.e., `insert()` = 70 − 35 = 35%) |
| Remaining (100 − 70 = 30) | Percentage of `delete()` operations                                                               |

### Example

The above command executes the benchmark with:

 8 threads
 200 K input range
 4- Multivalued(fatnode)size
 60 s runtime
 35 % contains, 35 % insert, and 30 % delete operations



