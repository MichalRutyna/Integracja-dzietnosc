import { useAppLogic } from '../../../hooks/useAppLogic';

const Logout = () => {
    const { handleLogout } = useAppLogic();
    return <button onClick={handleLogout} className="logout-button small-button">Logout</button>
}

export default Logout;
