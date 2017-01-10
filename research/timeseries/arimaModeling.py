import scipy
import scipy.stats
from pylab import *

import statsmodels.tsa.arima_model as arima
from statsmodels.tsa.arima_model import *


class ArimaModeling:
    def extractParams(self, series, order):
        n = len(series)
        model = arima.ARIMA(series, order).fit(method='mle', maxiter = 200)
        return(model.resid, model.params)

    def predict(self, series, params, order):
        n = len(series)
        model = arima.ARIMA(series, order).fit(start_params=params, maxiter = 0)
        #y_pred = model.predict(start=0, end=len(series)+1)
        y_pred = model.predict(start=0, end=len(series)-1)
        return(y_pred)

    def gaussProb(self, x, mu, sigma):
        x1 = (x-mu)/sigma
        prob = scipy.stats.norm(0.0,1.0).pdf(x1)
        return(prob)

    def gaussParameters(self, x):
        mu = mean(x)
        sigma = std(x)
        return(mu, sigma)

    def arima_prob_modeling(self, series, n):
        y = series[0:n-1]
        yx = series[0:n]

        xx = 0
        for ii in range(1,10):
            for jj in range(10):
                order = (ii,0,ii)
                try:
                    print (ii, jj)
                    [resid, params] = self.extractArimaParams(y, order)
                    y_pred = self.predict(yx, params, order)
                    xx = 1
                except:
                    yy = 0
                    if (xx == 1):
                        break
            if (xx == 1):
                break

        mu = mean(resid)
        sigma = std(resid)

        deltax = (series[0:len(y_pred)]-y_pred)
        deltax = yx-y_pred
        mu_x = mean(deltax)
        sigma_x = std(deltax)
        deltax = (deltax-mu_x)/sigma_x
        p = []
        for ii in range(len(deltax)):
            delta = deltax[ii]
            prob = self.gaussProb(delta, mu_x,sigma_x)
            p.append(prob)
            p = array(p)
        return(p, deltax)

    def genTimeSeriesDataVect(self, base_val=20, n=100, c=1.5, severity=15):
        series = base_val + c*rand(1,n)[0]
        z0 = mean(series) + 3*std(series) + severity
        z1 = base_val + c*rand(1,5)[0]
        t = []
        for ii in range(len(series)):
            t.append(series[ii])
        t.append(z0)
        for ii in range(len(z1)):
            t.append(z1[ii])

        series = np.array(t)
        return(series)


    def genTimeSeriesDataFile(self, severity, num_vects = 5):
        d = 99
        data = np.zeros((num_vects, d+6))
        fpOut = open("ts_data","w")
        for ii in range(5):
            vect = genTimeSeriesDataVect(20, d, 1.5, severity)
            for jj  in range(len(vect)):
                data[ii,jj] = vect[jj]

        savetxt('ts_data',data)



    def runTests(self, num_tests = 1, n=100):

        D = loadtxt('ts_data')
        ij = 1
        for test_id in range(num_tests):
            series = D[test_id,:]
            [p, delta_x] = arima_prob_modeling(series, n)
        
            x = range(0,len(series))
            plt.figure(ij), plt.hist(delta_x, 20)
            min_p = min(p)

            thr = 0.05
            I = where(delta_x > 3)[0]
            a = []
            b = []
            for ii in I:
                if (series[ii] > mean(series[0:n-1])):
                    a.append(ii)
                    b.append(series[ii])
            a = array(a)
            b = array(b)


            z = 0*series
            plt.figure(ij+1), plt.plot(x,series)
            plt.figure(ij+1), plt.plot(a, b, '*r')
            plt.figure(ij+1), plt.plot(x,z)
            ij += 2
        
        plt.show()
        return(p, delta_x)
    

    def runSingleTest(self, num_tests = 1, nx=100, offset=100):
        D = loadtxt('ts_data')
        ij = 1
        n = offset
        for test_id in range(num_tests):
            series = D[test_id,:]
            [p, delta_x] = arima_prob_modeling(series, n)

            x = range(0,len(series))
            plt.figure(ij), plt.hist(delta_x, 20)
            min_p = min(p)

            thr = 0.05
            I = where(delta_x > 3)[0]
        
            a = []
            b = []
            for ii in I:
                if (series[ii] > mean(series[0:n-1])):
                    a.append(ii)
                    b.append(series[ii])
            a = array(a)
            b = array(b)

            c = 0*series
            c[offset] = series[offset]

            z = 0*series
            plt.figure(ij+1), plt.plot(x[1:offset],series[1:offset])
            plt.figure(ij+1), plt.plot(a, b, '*r')
            plt.figure(ij+1), plt.plot(x[1:offset],z[1:offset])
            ij += 2
        
        plt.show()
        return(p, delta_x)
    
    
    def fit(self, series):
        flag = 0
        print (series)
        for ii in range(1,10):
            for jj in range(20):
                order = (ii,0,ii)
                try:
                    print (ii, jj)
                    [resid, params] = self.extractParams(series, order)
                    resid = resid[:-1]
                    flag = 1
                except:
                    yy = 0
                if (flag == 1):
                    break
            if (flag == 1):
                break
        if (flag == 0):
            resid = -1
            params = -1
            print ('not fitted')
        else:
            [mu, sigma] = self.gaussParameters(resid)            
            
        return(resid, params, order, mu, sigma)


    def predict_resid(self, series, test_points, params, order, mu, sigma):
        data = []
        for ii in range(len(series)):
            data.append(series[ii])
        data.append(0)
        data = np.array(data)
        n = len(data)
        pred = np.zeros(len(test_points))
        ii = 0
        for test_pt in test_points:
            data[n-1] = test_pt
            model = arima.ARIMA(data, order).fit(start_params=params, maxiter = 1)
            pred[ii] = model.predict(start=len(data)-1, end=len(data)-1)
            ii += 1

        resid = test_points-pred
        prob = np.zeros(len(resid))
        for ii in range(len(resid)):
            prob[ii] = self.gaussProb(resid[ii], mu, sigma)

        return(pred, resid, prob)
        

if __name__ == '__main__':
    A = ArimaModeling()
    series = np.random.poisson(5,200)
    [resid, params, order, mu, sigma] = A.fit(series)
    test_points = np.random.poisson(10,5)
    [y, new_resid, prob] = A.predict_resid(series, test_points, params, order, mu, sigma)
    
    

    
