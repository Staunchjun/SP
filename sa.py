import pandas as pd
import Primme, scipy.sparse
import numpy as np
import sys
filepath ="/Users/ruanwenjun/IdeaProjects/SP/Lmatrix.csv"
filepath2 ="/Users/ruanwenjun/IdeaProjects/SP/vec_matrix.csv"
Lmatrix = np.genfromtxt(filepath, delimiter=',')
k = int(sys.argv[1])

evals, evecs = Primme.eigsh(Lmatrix,k , tol=1e-3, which='SA')


evecs = pd.DataFrame(evecs)
evecs = evecs.transpose()
evecs.to_csv(filepath2,index=False)


