package by.bsuir.adam.wt.tasks.third.server.dao;

public class DAOFactory {
    private static final DAOFactory INSTANCE = new DAOFactory();

    private DAOFactory() {
    }

    public static DAOFactory getInstance() {
        return INSTANCE;
    }

    public CaseDAO getCaseDao() {
        return CaseDAO.getInstance();
    }

}
