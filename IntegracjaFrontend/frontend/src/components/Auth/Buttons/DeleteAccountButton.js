import { useAppLogic } from '../../../hooks/useAppLogic';

const DeleteAccountButton = () => {
  const { handleDeleteUser } = useAppLogic();
  return <button 
  onClick={() => {
    if (window.confirm('Are you sure you want to delete your account? This action cannot be undone.')) {
      handleDeleteUser();
    }
  }} 
  className="delete-account-button small-button"
>
  Delete Account
</button>
};

export default DeleteAccountButton;
