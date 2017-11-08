import pandas as pd
import Primme, scipy.sparse
import numpy as np
filepath ="/Users/ruanwenjun/IdeaProjects/SP/Lmatrix.csv"
Lmatrix = np.genfromtxt(filepath, delimiter=',')
evals, evecs = Primme.eigsh(Lmatrix, Lmatrix.shape[0], tol=1e-6, which='SA')
evecs = pd.DataFrame(evecs)
evecs.to_csv(filepath,index=False)