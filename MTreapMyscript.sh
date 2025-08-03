echo "90-9-1"
echo "90-9-1" >> MTreap_output.txt
fat_size=8
while [ $fat_size -le 128 ]
do
        range=200000
        while [ $range -le 20000000 ]
        do
        	n_th=1
            while [ $n_th -le 64 ]
            do
				iter=1
            	while [ $iter -le 3 ]
            	do
					if [ $n_th -le 8 ]
                  	then
			java M_Treap_Mytest2 $n_th $range 60000 $fat_size 90 99 >> MTreap_output.txt
                    elif [ $n_th -le 64 ]
                    then 
                    java M_Treap_Mytest2 $n_th $range 60000 $fat_size 90 99 >> MTreap_output.txt
                     fi
                ((iter=iter+1))
                done
           ((n_th=n_th*2))
           done
        ((range=range*10))
        done
((fat_size=fat_size*4))
done
echo "70-20-10"
echo "70-20-10" >> MTreap_output.txt
fat_size=8
while [ $fat_size -le 128 ]
do
        range=200000
        while [ $range -le 20000000 ]
        do
            n_th=1
            while [ $n_th -le 64 ]
            do
                iter=1
                while [ $iter -le 3 ]
                do
                    if [ $n_th -le 8 ]
                    then
                    java M_Treap_Mytest2 $n_th $range 60000 $fat_size 70 90 >> MTreap_output.txt
                    elif [ $n_th -le 64 ]
                    then
                    java M_Treap_Mytest2 $n_th $range 60000 $fat_size 70 90 >> MTreap_output.txt
                     fi
                ((iter=iter+1))
                done
           ((n_th=n_th*2))
           done
        ((range=range*10))
        done
((fat_size=fat_size*4))
done
echo "50-25-25"
echo "50-25-25" >> MTreap_output.txt
fat_size=8
while [ $fat_size -le 128 ]
do
        range=200000
        while [ $range -le 20000000 ]
        do
            n_th=1
            while [ $n_th -le 64 ]
            do
                iter=1
                while [ $iter -le 3 ]
                do
                    if [ $n_th -le 8 ]
                    then
                    java M_Treap_Mytest2 $n_th $range 60000 $fat_size 50 75 >> MTreap_output.txt
                    elif [ $n_th -le 64 ]
                    then
                    java M_Treap_Mytest2 $n_th $range 60000 $fat_size 50 75 >> MTreap_output.txt
                     fi
                ((iter=iter+1))
                done
           ((n_th=n_th*2))
           done
        ((range=range*10))
        done
((fat_size=fat_size*4))
done
echo "30-35-35"
echo "30-35-35" >> MTreap_output.txt
fat_size=8
while [ $fat_size -le 128 ]
do
        range=200000
        while [ $range -le 20000000 ]
        do
            n_th=1
            while [ $n_th -le 64 ]
            do
                iter=1
                while [ $iter -le 3 ]
                do
                    if [ $n_th -le 8 ]
                    then
                    java M_Treap_Mytest2 $n_th $range 60000 $fat_size 30 65 >> MTreap_output.txt
                    elif [ $n_th -le 64 ]
                    then
                    java M_Treap_Mytest2 $n_th $range 60000 $fat_size 30 65 >> MTreap_output.txt
                     fi
                ((iter=iter+1))
                done
           ((n_th=n_th*2))
           done
        ((range=range*10))
        done
((fat_size=fat_size*4))
done


