import React, { useEffect } from 'react';
import Sidenav from '../component/Layout/Sidenav';
import Box from '@mui/material/Box';
import Navbar from '../component/Layout/Navbar';
import Grid from '@mui/material/Grid';
import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import Typography from '@mui/material/Typography';
import Stack from '@mui/material/Stack';
import "../Dash.css";
import StorefrontIcon from '@mui/icons-material/Storefront';
import { useDispatch, useSelector } from 'react-redux';
import { showPromotion } from '../Store/promotionSlice';

function Home() {
  const dispatch = useDispatch();

  const { promotion, loading } = useSelector((state) => state.promotion);

  useEffect(() => {
    dispatch(showPromotion());
  }, [dispatch]);

  return (
    <>
      <Navbar />
      <Box height={70} />
      <Box sx={{ display: 'flex' }}>
        <Sidenav />
        <Box component="main" sx={{ flexGrow: 1, p: 3 }}>
          <Grid container spacing={2}>
            <Grid item xs={8}>
              <Stack spacing={2} direction="row">
                <Card sx={{ maxWidth: "49%", height: 140 }}>
                  <CardContent>
                    <Typography gutterBottom variant="h5" component="div">
                      Total Visitors
                    </Typography>
                    <Typography variant="body2" color="text.secondary">
                      Lorem ipsum dolor sit amet consectetur adipisicing elit. Sunt perspiciatis maxime reprehenderit deleniti odit! Nostrum reprehenderit.
                    </Typography>
                  </CardContent>
                </Card>
                <Card sx={{ maxWidth: "49%", height: 140 }}>
                  <CardContent>
                    <Typography gutterBottom variant="h5" component="div">
                      Conversion Rate
                    </Typography>
                    <Typography variant="body2" color="text.secondary">
                      conversionRate ? conversionRate.value : "No data available"
                      Lorem ipsum dolor sit amet consectetur, adipisicing elit. Ducimus, molestias. Nulla perspiciatis eligendi asperiores ea vero, expedita.
                    </Typography>
                  </CardContent>
                </Card>
              </Stack>
            </Grid>
            <Grid item xs={4}>
              <Stack spacing={2}>
                <Card sx={{ flex: 2, height: 200 }}>
                  <CardContent>
                    <Stack spacing={2} direction="row">
                      <StorefrontIcon />
                    </Stack>
                  </CardContent>
                </Card>
              </Stack>
            </Grid>
          </Grid>
          <Box height={20} />
          <Grid container spacing={2}>
            <Grid item xs={8}>
              <Card sx={{ height: "60vh" }}>
                <CardContent>
                  <Typography gutterBottom variant="h5" component="div">
                    Analytics
                  </Typography>
                  <Typography variant="body2" color="text.secondary">
                    Lorem ipsum dolor sit amet consectetur, adipisicing elit. Ducimus, molestias. Nulla perspiciatis eligendi asperiores ea vero, expedita ut itaque earum.
                  </Typography>
                  <Card sx={{ mt: 2 }}>
                    <CardContent>
                      {loading ? "Loading..." : promotion.map((promo) => (
                        <Card key={promo.id} sx={{ mb: 2 }}>
                          <CardContent>
                            <Typography variant="body2" color="text.secondary">
                              Promotion ID: {promo.id}
                            </Typography>
                            <Typography variant="body2" color="text.secondary">
                              Promotion Type: {promo.promotionType}
                            </Typography>
                          </CardContent>
                        </Card>
                      ))}
                    </CardContent>
                  </Card>
                </CardContent>
              </Card>
            </Grid>
          </Grid>
        </Box>
      </Box>
    </>
  );
}

export default Home;
