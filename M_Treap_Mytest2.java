/* Testing a Concurrent Treap with Fat_node */
import java.util.concurrent.ThreadLocalRandom;
import java.util.Map;

//import java.util.concurrent.atomic.AtomicInteger;
public class M_Treap_Mytest2{
	private static int RANGE;
	private static int THREADS;
	private static int PER_THREAD;
	private static int TIME;
	private static int FATSIZE;

	Concurrent_MTreap2 instance;
	long []opCount;
	long totalOps;
	Thread []th;
	long start;
	int s_Limit,i_Limit;

	public M_Treap_Mytest2(int num_threads, int range, int time, int fat_node_size, int arg1, int arg2)
	{
		instance=new Concurrent_MTreap2(num_threads, range, fat_node_size);
		//instance=new ConTreap2(num_threads);
		THREADS=num_threads;
		RANGE=range;
		TIME=time;
		FATSIZE=fat_node_size;
		th=new Thread[num_threads];
		opCount=new long[num_threads];
		totalOps=0;
		s_Limit=arg1;
		i_Limit=arg2;
	}
	public void prefill() throws Exception{
		for(int i=0;i<1;i++)
		{
			th[i]=new Fill();
		}
		for(int i=0;i<1;i++)
		{
			th[i].start();
		}
		for(int i=0;i<1;i++)
		{
			th[i].join();
		}
	}


class Fill extends Thread
	{
		int PER_THREAD_PREFILL=RANGE/2;
		public void run()
		{	
		   
			
			for(int i=0;i<PER_THREAD_PREFILL;)
			{
				
				int val=ThreadLocalRandom.current().nextInt(RANGE);
				
				boolean ins=instance.insert(val, 0);
				{ i=i+1;}
				
			}
			
		}
	}
	public void testParallel()throws Exception{
		for(int i=0;i<THREADS;i++)
		{
			th[i]=new AllMethods();
		}
		start=System.currentTimeMillis();
		 for(int i=0;i<THREADS;i++)
         {
            th[i].start();
         }
		 for(int i=0;i<THREADS;i++)
         {
            th[i].join();
         }
	}
	class AllMethods extends Thread{
		public void run()
		{
			long count=0;
			
			long end=System.currentTimeMillis();
			int WARMUP_TIME=5000;
           		for(;(end-start)<=WARMUP_TIME;end=System.currentTimeMillis()){
				int val=ThreadLocalRandom.current().nextInt(RANGE);
				instance.contains(val);
				
			}
			
			end=System.currentTimeMillis();
			int TOTAL_TIME=WARMUP_TIME+TIME;
			for(int i=0;(end-start)<=TOTAL_TIME;i=i+1)
			{

				
				if(i%100==99){  end=System.currentTimeMillis(); }
				int ch=0;
				int chVal=ThreadLocalRandom.current().nextInt(99);
				if(chVal<s_Limit){ ch=0; }
				else if((chVal>=s_Limit)&&(chVal<i_Limit)) ch=1;
				else ch=2;
				int val=ThreadLocalRandom.current().nextInt(RANGE);
				
				switch(ch){

					case 0:{
						boolean exits=instance.contains(val);
						
						} break;
					case 1: {
						
						 boolean ins=instance.insert(val, 0);
						
					
					}
						break;
					case 2:{
							 
					boolean ins=instance.remove(val); 
						
					}
					break;
					default: break;
				}
				
				count=count+1; 
			}
			opCount[ThreadID.get()]=count;
		
	}
}
	public long totalOperations()
	{
		for(int i=0;i<THREADS;i++)
		{
			totalOps=totalOps+opCount[i];
		}
		return totalOps;
	}
	
	public static void main(String[] args){ 
		int num_threads=Integer.parseInt(args[0]);
		int range=Integer.parseInt(args[1]);
		int time=Integer.parseInt(args[2]);
		int fatSize=Integer.parseInt(args[3]);
		int s_Limit=Integer.parseInt(args[4]);
		int i_Limit=Integer.parseInt(args[5]);
		
		M_Treap_Mytest2 ob=new M_Treap_Mytest2(num_threads,range,time,fatSize,s_Limit,i_Limit);
		try{ ob.prefill(); }catch(Exception e){ System.out.println(e); }
		
		try{	ob.testParallel(); }catch(Exception e){ System.out.println(e); }
		long total_Operations=ob.totalOperations();
		double throughput=(total_Operations/(1000000.0*time))*1000;// Millions of Operations per second
		System.out.println("\t:num_threads:"+num_threads+"\t:range:"+range+"\t:total_Operations:"+total_Operations+"\t:throughput:"+throughput+"\t");		
		//ob.display();	
		System.exit(num_threads);
	
	}
}	
