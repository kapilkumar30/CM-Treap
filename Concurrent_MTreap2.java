/* Concurrent implementation of MTreaps*/
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
class Concurrent_MTreap2
{
	public static int FATSIZE;
	//public static int SHIFT;
	public static int RANGE;
	public static int NUM_THREADS;
	final Node min, max;
	volatile boolean stop = false;
	private MaintenanceThread mainThd;
	class  Node{
		final int key;
		volatile int pri;// Priority of a node
		public ConcurrentHashMap <Integer,Integer>instance;
		volatile boolean mark; // whether a node is deleted or not
		volatile boolean transit1,transit2;
		volatile Node left, right, parent;// left- left subtree ref. right - right subtree ref. parent - parent node ref.
		volatile Node pred, succ; // pred and succ are for predecessor and successor of a node.
		//volatile ReentrantLock A;
		volatile ReentrantReadWriteLock RWLock_Node;
		public Node(int data1, int p)
		{
			int size=FATSIZE;
			instance=new ConcurrentHashMap <Integer,Integer>(size,0.75f,NUM_THREADS);
			instance.put(data1,data1);
			this.key=data1/FATSIZE;
			this.pri=p;
			this.transit1=false;
			this.transit2=false;
			this.left=null;
			this.right=null;
			this.parent=null;
			this.pred=null;
			this.succ=null;
			this.RWLock_Node = new ReentrantReadWriteLock();
			//A=new ReentrantLock();
		}			
	}
	
	
	public Node root=null;
	
	public Concurrent_MTreap2(int num_threads, int range, int fat_node_size)
	{
		FATSIZE=fat_node_size;
		RANGE=range;
		NUM_THREADS=num_threads;
		root=new Node(Integer.MAX_VALUE, Integer.MAX_VALUE);
		root.left=new Node(Integer.MAX_VALUE-1, Integer.MAX_VALUE-1);
		root.left.left=new Node(Integer.MIN_VALUE,Integer.MIN_VALUE);
		root.left.parent=root;
		root.pred=root.left;
		root.left.succ=root;
		root.left.pred=root.left.left;
		root.left.left.succ=root.left;
		root.left.left.parent=root.left;
		root.transit1=false;	
		root.left.transit1=false;
		root.left.left.transit1=false;
		min=root.left.left;
		max=root.left;
		this.startMaintenance();
	}
	private class MaintenanceThread extends Thread {
		Concurrent_MTreap2 m;

		MaintenanceThread(Concurrent_MTreap2 m) {
			this.m = m;
		}

		public void run() {
			m.doMaintenance();
		}
	}

public boolean startMaintenance() {
		this.stop = false;

		mainThd = new MaintenanceThread(this);

		mainThd.start();

		return true;
	}
boolean doMaintenance() {
		 		
		while (!stop) {
			
			reConTree();
			
		}
		return true;
		}

void reConTree()
{

Node node = null, parent = null, Lnode;
    Node pred, succ;
int k;
node=min;
node=node.succ;
k=node.key;
while(node != max)
  {

if(node!=null && node.instance.isEmpty()==true){
	while(true){
	  if(node.left!=null && node.right!=null){break;}
Node p=(node.key>=k)?node.pred:node;
		if(!p.RWLock_Node.writeLock().tryLock()){  continue; }
		
		Node s=p.succ;
		if((k>p.key)&&(k<=s.key))
		{
			if(k<s.key){
				p.RWLock_Node.writeLock().unlock();
				break;
			}
			
			if(s!= node || !s.RWLock_Node.writeLock().tryLock()){ p.RWLock_Node.writeLock().unlock(); continue; }
			    
			if(s.transit1){ p.RWLock_Node.writeLock().unlock(); s.RWLock_Node.writeLock().unlock(); continue; }
			Node sSucc=s.succ;
			//if(sSucc.mark){ p.RWLock_Node.writeLock().unlock(); s.RWLock_Node.writeLock().unlock(); continue; }
			s.transit2=true;
			
			Node n=s;
			
			boolean  flag=acquireTreeLocks(n,p);
			while(flag){
				n.mark=true;
				sSucc.pred=p;
				p.succ=sSucc;
				Node child=(n.left!=null)?n.left:n.right;
				updateChild(n.parent,n,child);
				if((child!=null&&child!=p)&&(n.parent!=p)||(child==null&&n.parent!=p)){p.RWLock_Node.writeLock().unlock(); }
				if(child!=null){  child.RWLock_Node.writeLock().unlock();  }
				n.parent.RWLock_Node.writeLock().unlock(); 
				n.RWLock_Node.writeLock().unlock();
				break;	
			
			}
			if(!flag){ p.RWLock_Node.writeLock().unlock(); s.RWLock_Node.writeLock().unlock(); continue; }
			
		}else{p.RWLock_Node.writeLock().unlock(); break;}
     }} 
node=node.succ;
 k=node.key;
  }

return ;
}

void updateChild(Node parent, Node oldCh, Node newCh)
{
	if(parent.left==oldCh)
	{parent.left=newCh;}
	else
	{parent.right=newCh;}
	if(newCh!=null){ newCh.parent=parent; }
}

	
public boolean insert(int x, int pri)
	{
		Node node, p, s, newNode, parent;
			int k,mod;
		while(true)
		{
			//Node node,p,s,parent,newNode;// p is predecessor of a node, s is successor of a node. newNode is newly inserted node.
			node=search(x);// If treap contains a node with value x then return that. Otherwise return the terminated node.
			k=x/FATSIZE;

			if(node.key==k && node.transit1==false && node.transit2==false && node.mark==false)
				{
			if(!node.RWLock_Node.readLock().tryLock()){continue;}
			   Integer val1=node.instance.putIfAbsent(x,x);
				node.RWLock_Node.readLock().unlock();
				if(val1==null){ return true;} 
				else{return false;}
				}


			parent=node.parent;
			p=(node.key>=k)?node.pred:node;
			if(!p.RWLock_Node.writeLock().tryLock())
			{
				continue; 
			}
			if(p.transit2){ p.RWLock_Node.writeLock().unlock(); continue; }
			s=p.succ;
			if(s.mark){ p.RWLock_Node.writeLock().unlock(); continue;  }
			if(p!=node&&!node.RWLock_Node.writeLock().tryLock())
				{
					p.RWLock_Node.writeLock().unlock();
					continue; 
				}
			if((k>p.key)&&(k<=s.key))
			{
				if(k==s.key)
				{
					if(s.transit2){
						if(p!=node) { node.RWLock_Node.writeLock().unlock(); }
						p.RWLock_Node.writeLock().unlock(); continue;
					}
					else{
						Integer val1=s.instance.putIfAbsent(x,x);
						if(p!=node) { node.RWLock_Node.writeLock().unlock(); }
						p.RWLock_Node.writeLock().unlock(); 
						if(val1==null){ return true;} 
						return false;
					}
				}
				//if(!s.trySuccLock()){ p.succUnlock(); continue; }
				
				if(node.transit1||node.transit2||parent!=node.parent)
				{
					p.RWLock_Node.writeLock().unlock();
					if(p!=node) { node.RWLock_Node.writeLock().unlock(); }
					continue;
				}
				else if(((k<node.key)&&(node.left==null))||((k>node.key)&&(node.right==null)))
				{	
						newNode=new Node(x,pri);
						newNode.parent=node;
						newNode.succ=s;
						newNode.pred=p;

						s.pred=newNode;
						p.succ=newNode;
						
						if(k<node.key)
						{
							node.left=newNode;
						}
						else
						{
							node.right=newNode;
						}
						p.RWLock_Node.writeLock().unlock();
						if(p!=node){ node.RWLock_Node.writeLock().unlock();}
						return true;
				}
				else
				{
					p.RWLock_Node.writeLock().unlock();
					if(p!=node) { node.RWLock_Node.writeLock().unlock(); }
					continue;
				}
			}
			if(p!=node) { node.RWLock_Node.writeLock().unlock(); }
			p.RWLock_Node.writeLock().unlock();
		}
	}

// Newly inserted node is moved up, until node.parent priority is greater than node.priority. 
public void adjustHeap(Node node)
{
	while(true)
	{
			Node nParentParent=node.parent.parent;
			if(!nParentParent.RWLock_Node.writeLock().tryLock()){ 
				continue;
			 }
			
			Node nParent=node.parent;
			if(!nParent.RWLock_Node.writeLock().tryLock()){ 
				nParentParent.RWLock_Node.writeLock().unlock();
				continue;
			}
			if(nParent.transit1||nParent.transit2)
			{
				nParentParent.RWLock_Node.writeLock().unlock();  nParent.RWLock_Node.writeLock().unlock();
				continue;
			}
			if(!node.RWLock_Node.writeLock().tryLock()){ nParentParent.RWLock_Node.writeLock().unlock();nParent.RWLock_Node.writeLock().unlock();
				continue; 
			}	
			if((nParentParent!=node.parent.parent)||(nParent!=node.parent)){
				nParentParent.RWLock_Node.writeLock().unlock();
				nParent.RWLock_Node.writeLock().unlock();
				node.RWLock_Node.writeLock().unlock();
				continue;
			}
			if(nParent.pri<node.pri)
			{
				boolean flag=false;
					if(nParent.left==node)
					{
						if(node.right!=null)
						{
							Node nRight=node.right;
							Node nRightParent=node.right.parent;
							if(!nRight.RWLock_Node.writeLock().tryLock())
							{ 
								 nParentParent.RWLock_Node.writeLock().unlock();
                               					 nParent.RWLock_Node.writeLock().unlock();
                                				 node.RWLock_Node.writeLock().unlock();
                                				 continue;

							}
							if((nRight!=node.right)||nRightParent!=node)
							{
								nParentParent.RWLock_Node.writeLock().unlock();
				                                nParent.RWLock_Node.writeLock().unlock();
                                				node.RWLock_Node.writeLock().unlock();
								nRight.RWLock_Node.writeLock().unlock();;
								continue;
							}
							flag=true;
						}
			 			singleRotateRight(nParent, node);
						if(flag==true){ nParent.left.RWLock_Node.writeLock().unlock();  }
						nParent.RWLock_Node.writeLock().unlock();
										
					}
					else
					{
						if(node.left!=null)
						{
							Node nLeft=node.left,nLeftParent=node.left.parent;
							if(!nLeft.RWLock_Node.writeLock().tryLock())
							{
								 nParentParent.RWLock_Node.writeLock().unlock();
				                                 nParent.RWLock_Node.writeLock().unlock();
                                				 node.RWLock_Node.writeLock().unlock();
                                 				 continue;

							}
							if((nLeft!=node.left)||nLeftParent!=node)
							{
								nParentParent.RWLock_Node.writeLock().unlock();
                                				nParent.RWLock_Node.writeLock().unlock();
                                				node.RWLock_Node.writeLock().unlock();
								nLeft.RWLock_Node.writeLock().unlock();
								continue;
							}
							flag=true;
						}
						singleRotateLeft(nParent,node);
						if(flag==true){ nParent.right.RWLock_Node.writeLock().unlock();  }
						nParent.RWLock_Node.writeLock().unlock();
					}
					nParentParent.RWLock_Node.writeLock().unlock();
					node.RWLock_Node.writeLock().unlock();
			}
			else
			{
				node.transit1=false;
				
				nParentParent.RWLock_Node.writeLock().unlock();
                                nParent.RWLock_Node.writeLock().unlock();
                                node.RWLock_Node.writeLock().unlock();
				break;
			}
		}
}
public void singleRotateRight(Node parent, Node node)
{
        Node temp=parent.parent;
        if(temp.left==parent)
        {
                temp.left=node;
        }
        else{
                temp.right=node;
        }
        node.parent=temp;
        Node nodeRight=node.right;
        node.right=parent;
        parent.left=nodeRight;
        if(nodeRight!=null)
        {
                nodeRight.parent=parent;
        }
        parent.parent=node;
        
}
// Rotate Left.
public void singleRotateLeft(Node parent, Node node)
{
        Node temp=parent.parent;
                if(temp.left==parent)//parent.parent left child is parent
                {
                        temp.left=node;
                }
                else{//parent.parent right child is  parent
                        temp.right=node;
                }
        node.parent=temp;
        Node nodeLeft=node.left;
        node.left=parent;
        parent.right=nodeLeft;
        if(nodeLeft!=null)
        {
                nodeLeft.parent=parent;
        }
        parent.parent=node;
}

public Node search(int val)
	{

	int x=val/FATSIZE;
        Node T, child;
        T=root;
	int depth=0;
        while(T!=null)
        {
                int key=T.key;
                if(key==x) return T;
                if(key>x){
                        child=T.left;
                }
                else{
                        child=T.right;
                }
                if(child!=null){ T=child; }
                else{ return T; }
		

        }
                return T;		
	}

    public boolean contains(int val) {
        
        Node node = search(val);
        int k = val / FATSIZE;

        while (node.key > k) {node = node.pred;}
        while (node.key < k) {node = node.succ;}
        if (node.key == k) {
            boolean flag = node.instance.containsKey(val);
            if (flag) {
                while (!node.RWLock_Node.writeLock().tryLock()) {continue;}
		
                if(node.pri < Integer.MAX_VALUE - 1) {
                    node.pri = node.pri + 1;
		    if(node.pri<=node.parent.pri){node.RWLock_Node.writeLock().unlock(); return true;}
		    node.transit1=true;
		    node.RWLock_Node.writeLock().unlock();
                	adjustHeap(node);
                }else{node.RWLock_Node.writeLock().unlock(); return true;}
                
                return true;
            }
        }
        return false;
    }

public boolean remove(int val)
	{
		Node node=search(val);
		int k=val/FATSIZE;
		while(node.key>k){ node=node.pred; }
		while(node.key<k){ node=node.succ; }
		if(node.key==k && node.transit1==false && node.transit2==false && node.mark==false)
		{
				if(node.instance.size()>0){
				while(!node.RWLock_Node.readLock().tryLock()){continue;}
			   	boolean flag=node.instance.remove(val,val);
			   	node.RWLock_Node.readLock().unlock(); 
				return flag;}
				if(node.instance.isEmpty()==true){
					while(!node.RWLock_Node.writeLock().tryLock()){continue;}
					 node.pri=0;
						node.RWLock_Node.writeLock().unlock();
					   
					}
		}	
		return false;
	}

boolean acquireTreeLocks(Node n,Node p)
{
	int i=0;
	while(true&&i<100)
	{
		i=i+1;
		Node nParent=n.parent;
			if(nParent!=p&&!nParent.RWLock_Node.writeLock().tryLock()){ continue; }
			if(nParent!=n.parent){ if(nParent!=p){ nParent.RWLock_Node.writeLock().unlock();}/*System.out.println("Hello");*/ continue; }
	
		if(n.left==null||n.right==null)
		{
			Node child=null;
			if(n.right!=null)
			{
				child=n.right; 
			}
			if(n.left!=null)
			{
				child=n.left;
			}
			if(child!=null)
			{
				if(child!=p&&!child.RWLock_Node.writeLock().tryLock()){ if(nParent!=p){ nParent.RWLock_Node.writeLock().unlock();}  continue; }
				if((child.parent!=n)){  if(nParent!=p){ nParent.RWLock_Node.writeLock().unlock();} if(child!=p){ child.RWLock_Node.writeLock().unlock();} continue; }
			}
			return true;
		}
		else{
			if(n.left.pri>n.right.pri){
				Node child1=n.left; 
				Node parent=child1.parent;
					if(child1!=p&&!child1.RWLock_Node.writeLock().tryLock()){  if(nParent!=p){ nParent.RWLock_Node.writeLock().unlock();} continue; }
					if((child1!=n.left)||(parent!=child1.parent)){ 
						if(nParent!=p){ nParent.RWLock_Node.writeLock().unlock();}
						if(child1!=p){ child1.RWLock_Node.writeLock().unlock();}
						continue; 
					}
				if(child1.right!=null){
					Node child1_Right=child1.right; 
					Node R_parent=child1_Right.parent;
						if(child1_Right!=p&&!child1_Right.RWLock_Node.writeLock().tryLock()){
							if(nParent!=p){nParent.RWLock_Node.writeLock().unlock();}
							if(child1!=p){child1.RWLock_Node.writeLock().unlock();}
							continue;
						}
						if((child1_Right!=child1.right)||(R_parent!=child1_Right.parent)){
							if(nParent!=p){nParent.RWLock_Node.writeLock().unlock();}
							if(child1_Right!=p){ child1_Right.RWLock_Node.writeLock().unlock();}
							if(child1!=p){child1.RWLock_Node.writeLock().unlock();}
							continue; 
						}
					
				}
			}
			else{
				Node child1=n.right;
				Node parent=child1.parent;
					if(child1!=p&&!child1.RWLock_Node.writeLock().tryLock()){ nParent.RWLock_Node.writeLock().unlock();  continue; }
					if((child1!=n.right)||(parent!=child1.parent)){ 
						 if(nParent!=p){ nParent.RWLock_Node.writeLock().unlock(); }
						if(child1!=p){ child1.RWLock_Node.writeLock().unlock(); }
						continue; 
					}
				if(child1.left!=null){
					Node child1_Left=child1.left;
					Node L_parent=child1_Left.parent;
					if(child1_Left!=p&&!child1_Left.RWLock_Node.writeLock().tryLock()){ 
						if(nParent!=p){ nParent.RWLock_Node.writeLock().unlock();}
						 if(child1!=p){ child1.RWLock_Node.writeLock().unlock(); }
						continue;
					}
					if((child1_Left!=child1.left)||(L_parent!=child1_Left.parent)){
						 if(nParent!=p){ nParent.RWLock_Node.writeLock().unlock(); }
						 if(child1_Left!=p){ child1_Left.RWLock_Node.writeLock().unlock();}
						  if(child1!=p){ child1.RWLock_Node.writeLock().unlock(); }
						 continue; 
					}
				}
			}
			
			return true;
		}
	}
	//System.out.println("Value of i:"+i);
	return false;
}

}


