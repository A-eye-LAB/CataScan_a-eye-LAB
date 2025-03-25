import { permanentRedirect } from 'next/navigation';

function Home() {
    permanentRedirect(`/reports`); // Navigate to the new user profile

    return <></>;
}

export default Home;
